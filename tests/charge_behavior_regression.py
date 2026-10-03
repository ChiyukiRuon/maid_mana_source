"""Compile and execute the real ChargeBehavior against isolated Java test doubles.

Run: python tests/charge_behavior_regression.py
Requires Python 3 and a JDK supporting --release 17. No game or test dependencies.
This tests charging control flow, not Forge registration or in-game integration.
"""

from pathlib import Path
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[1]
PACKAGE = "com.chiyukiruon.maid_mana_source"
SOURCE = ROOT / "src/main/java" / Path(*PACKAGE.split(".")) / "behavior/ChargeBehavior.java"

# Only the external collaborators are replaced; ChargeBehavior is compiled unchanged.
STUBS = {
    "org.jetbrains.annotations.NotNull": "public @interface NotNull {}",
    "net.minecraft.core.BlockPos": "public record BlockPos(int getX, int getY, int getZ) {}",
    "net.minecraft.core.particles.ParticleTypes": "public class ParticleTypes { public static final Object HAPPY_VILLAGER = new Object(); }",
    "net.minecraft.sounds.SoundEvents": "public class SoundEvents { public static final Object EXPERIENCE_ORB_PICKUP = new Object(); }",
    "net.minecraft.sounds.SoundSource": "public enum SoundSource { BLOCKS }",
    "net.minecraft.world.level.block.entity.BlockEntity": "public class BlockEntity {}",
    "net.minecraft.world.level.block.state.BlockState": "public class BlockState {}",
    "net.minecraftforge.fml.ModList": "public class ModList { public static ModList get() { return new ModList(); } public boolean isLoaded(String id) { return false; } }",
    "com.hollingsworth.arsnouveau.api.source.ISourceTile": "public interface ISourceTile { boolean canAcceptSource(); int addSource(int amount); }",
    "net.minecraft.server.level.ServerLevel": """
        import java.util.*;
        import net.minecraft.core.BlockPos;
        import net.minecraft.world.level.block.entity.BlockEntity;
        import net.minecraft.world.level.block.state.BlockState;
        public class ServerLevel {
            public final Map<BlockPos, BlockEntity> blocks = new HashMap<>();
            public long getGameTime() { return 100; }
            public BlockEntity getBlockEntity(BlockPos pos) { return blocks.get(pos); }
            public BlockState getBlockState(BlockPos pos) { return new BlockState(); }
            public void sendParticles(Object particle, double x, double y, double z,
                    int count, double dx, double dy, double dz, double speed) {}
            public void playSound(Object player, BlockPos pos, Object sound, Object category,
                    float volume, float pitch) {}
        }
    """,
    "net.minecraft.world.entity.ai.behavior.Behavior": """
        import java.util.Map;
        import net.minecraft.server.level.ServerLevel;
        public class Behavior<T> {
            public Behavior(Map<?, ?> memories) {}
            protected boolean checkExtraStartConditions(ServerLevel level, T entity) { return true; }
            protected void start(ServerLevel level, T entity, long time) {}
        }
    """,
    PACKAGE + ".Config": """public class Config {
        public static int maxPerCharge = 200, coolingTime = 20, favorChargeBonus,
                favorCooldownReduction, chargeParticleCount;
        public static boolean enableFavorEffect, chargingCompletedSound;
        public static double chargeParticleRadius;
    }""",
    PACKAGE + ".data.MaidChargeConfig": """public class MaidChargeConfig {
        public static final Object KEY = new Object();
        public record Data(boolean chargeMode, boolean chargeStrategy) {
            public static Data getDefault() { return new Data(true, true); }
        }
    }""",
    PACKAGE + ".registry.MemoryModuleRegistry": """public class MemoryModuleRegistry {
        public static final Key CHARGE_INDEX = new Key();
        public static class Key { public Object get() { return this; } }
    }""",
    PACKAGE + ".memory.ChargeSourceListMemory": """
        import java.util.List;
        import net.minecraft.core.BlockPos;
        import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
        public record ChargeSourceListMemory(List<BlockPos> getJars) {
            public static ChargeSourceListMemory getMemory(EntityMaid maid) { return maid.memory; }
        }
    """,
    "com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid": """
        import java.util.*;
        import com.chiyukiruon.maid_mana_source.data.MaidChargeConfig;
        import com.chiyukiruon.maid_mana_source.memory.ChargeSourceListMemory;
        public class EntityMaid {
            public MaidChargeConfig.Data config;
            public ChargeSourceListMemory memory;
            public final Brain brain = new Brain();
            public int getId() { return 1; }
            public MaidChargeConfig.Data getOrCreateData(Object key, MaidChargeConfig.Data fallback) { return config; }
            public Brain getBrain() { return brain; }
            public Favor getFavorabilityManager() { return new Favor(); }
            public static class Favor { public int getLevel() { return 0; } }
            public static class Brain {
                public Integer index;
                public Optional<Integer> getMemory(Object key) { return Optional.ofNullable(index); }
                public void setMemory(Object key, int value) { index = value; }
            }
        }
    """,
    PACKAGE + ".util.TargetUtil": """
        import java.util.List;
        import net.minecraft.core.BlockPos;
        import net.minecraft.server.level.ServerLevel;
        import net.minecraft.world.level.block.state.BlockState;
        import com.hollingsworth.arsnouveau.api.source.ISourceTile;
        public class TargetUtil {
            public static int getChargeAmount(ServerLevel level, List<BlockPos> positions) {
                int count = 0;
                for (BlockPos pos : positions)
                    if (level.getBlockEntity(pos) instanceof ISourceTile tile && tile.canAcceptSource()) count++;
                return count;
            }
            public static boolean isBlockFromMod(BlockState state, String id) { return false; }
        }
    """,
    PACKAGE + ".util.MaidAiUtil": """
        import java.util.*;
        import net.minecraft.core.BlockPos;
        import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
        public class MaidAiUtil {
            public static final List<BlockPos> targets = new ArrayList<>();
            public static void setWalkAndLookTargetMemories(EntityMaid maid, BlockPos pos, double speed) { targets.add(pos); }
        }
    """,
    PACKAGE + ".advancement.AdvancementTypes": """
        import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
        public class AdvancementTypes {
            public static final Object CHARGE_MANA_POOL = new Object(), MAID_CHARGE = new Object();
            public static void triggerForMaid(EntityMaid maid, Object advancement) {}
        }
    """,
}

HARNESS = """
package com.chiyukiruon.maid_mana_source.behavior;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.chiyukiruon.maid_mana_source.data.MaidChargeConfig;
import com.chiyukiruon.maid_mana_source.memory.ChargeSourceListMemory;
import com.chiyukiruon.maid_mana_source.util.MaidAiUtil;

public class ChargeBehaviorRegression {
    // Represents a pedestal's relevant property: a BlockEntity without ISourceTile.
    static class NonSourceBlockEntity extends BlockEntity {}
    static class Jar extends BlockEntity implements ISourceTile {
        int source;
        Jar(int source) { this.source = source; }
        public boolean canAcceptSource() { return source < 1000; }
        public int addSource(int amount) { source += amount; return source; }
    }
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        String mode = args[0], replacement = args[1];
        ServerLevel level = new ServerLevel();
        EntityMaid maid = new EntityMaid();
        if (mode.equals("index")) {
            int storedIndex = Integer.parseInt(replacement);
            BlockPos a = new BlockPos(0, 0, 0), b = new BlockPos(1, 0, 0), c = new BlockPos(2, 0, 0);
            Jar[] jars = {new Jar(0), new Jar(0), new Jar(0)};
            List<BlockPos> positions = List.of(a, b, c);
            for (int i = 0; i < positions.size(); i++) level.blocks.put(positions.get(i), jars[i]);
            maid.memory = new ChargeSourceListMemory(positions);
            maid.config = new MaidChargeConfig.Data(true, true);
            maid.brain.index = storedIndex;
            ChargeBehavior behavior = new ChargeBehavior();
            behavior.start(level, maid, 100);
            int expected = Math.floorMod(storedIndex, 3);
            for (int i = 0; i < jars.length; i++)
                check(jars[i].source == (i == expected ? 200 : 0), "wrong jar for stored index " + storedIndex);
            check(Objects.equals(maid.brain.index, (expected + 1) % 3), "index must remain normalized");
            behavior.start(level, maid, 101);
            check(jars[(expected + 1) % 3].source == 200, "next round must charge next jar");
            System.out.println("PASS stored index " + storedIndex);
            return;
        }
        BlockPos stale = new BlockPos(0, 0, 0), fullPos = new BlockPos(1, 0, 0),
                firstPos = new BlockPos(2, 0, 0), secondPos = new BlockPos(3, 0, 0);
        Jar original = new Jar(0), full = new Jar(1000), first = new Jar(0), second = new Jar(0);
        level.blocks.put(stale, original);
        maid.memory = new ChargeSourceListMemory(List.of(stale, fullPos, firstPos, secondPos));
        // The scan remembered a source jar; the world changes before charging starts.
        if (replacement.equals("non-source")) level.blocks.put(stale, new NonSourceBlockEntity());
        else level.blocks.remove(stale);
        level.blocks.put(fullPos, full);
        level.blocks.put(firstPos, first);
        level.blocks.put(secondPos, second);
        maid.config = new MaidChargeConfig.Data(!mode.equals("batch"), mode.equals("round-robin"));
        ChargeBehavior behavior = new ChargeBehavior();
        behavior.start(level, maid, 100);
        if (mode.equals("round-robin")) {
            check(Objects.equals(maid.brain.index, 1), "invalid target must advance round-robin index");
            check(first.source == 0 && second.source == 0 && MaidAiUtil.targets.isEmpty(), "invalid target must have no charging/movement effects");
            behavior.start(level, maid, 101);
            check(Objects.equals(maid.brain.index, 2), "full target must advance index");
            behavior.start(level, maid, 102);
            check(Objects.equals(maid.brain.index, 3), "valid target must advance index");
        }
        check(original.source == 0, "removed jar must not be charged");
        check(full.source == 1000, "full jar must not be charged");
        if (mode.equals("batch")) {
            check(first.source == 100 && second.source == 100, "batch must divide 200 among two valid jars");
            check(MaidAiUtil.targets.equals(List.of(firstPos, secondPos)), "batch must visit only chargeable jars");
        } else {
            check(first.source == 200 && second.source == 0, "single mode must charge only first eligible jar");
            check(MaidAiUtil.targets.equals(List.of(firstPos)), "single mode must visit only charged jar");
        }
        System.out.println("PASS " + mode + " / " + replacement);
    }
}
"""


class ChargeBehaviorRegressionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temp = tempfile.TemporaryDirectory(prefix="charge-regression-")
        cls.addClassCleanup(cls.temp.cleanup)
        cls.directory = Path(cls.temp.name)
        fixtures = cls.directory / "fixtures"
        files = []
        for name, body in STUBS.items():
            package, _, simple = name.rpartition(".")
            path = fixtures / Path(*package.split(".")) / (simple + ".java")
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(f"package {package};\n{body}", encoding="utf-8")
            files.append(path)
        harness = fixtures / "ChargeBehaviorRegression.java"
        harness.write_text(HARNESS, encoding="utf-8")
        files.append(harness)
        fixed = SOURCE.read_text(encoding="utf-8")
        # Restore each original unchecked cast to prove the regression is detected.
        old = fixed.replace(
            "if (!(level.getBlockEntity(pos) instanceof ISourceTile jar)) continue;",
            "ISourceTile jar = (ISourceTile) level.getBlockEntity(pos); if (jar == null) continue;",
        ).replace(
            "if (!(level.getBlockEntity(pos) instanceof ISourceTile jar)) {",
            "ISourceTile jar = (ISourceTile) level.getBlockEntity(pos); if (jar == null) {",
        )
        if old == fixed:
            raise AssertionError("Mutation did not restore unchecked casts")
        for version, source in (("fixed", fixed), ("unchecked", old)):
            source_file = cls.directory / version / "ChargeBehavior.java"
            source_file.parent.mkdir()
            source_file.write_text(source, encoding="utf-8")
            result = subprocess.run(
                ["javac", "--release", "17", "-encoding", "UTF-8", "-d",
                 str(cls.directory / version / "classes"), *map(str, files), str(source_file)],
                capture_output=True, text=True,
            )
            if result.returncode:
                raise AssertionError(result.stdout + result.stderr)

    def run_case(self, version, mode, replacement):
        return subprocess.run(
            ["java", "-cp", str(self.directory / version / "classes"),
             PACKAGE + ".behavior.ChargeBehaviorRegression", mode, replacement],
            capture_output=True, text=True,
        )

    def test_all_modes_skip_replaced_or_removed_targets(self):
        for mode in ("batch", "round-robin", "sequential"):
            for replacement in ("non-source", "removed"):
                with self.subTest(mode=mode, replacement=replacement):
                    result = self.run_case("fixed", mode, replacement)
                    self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
                    print(result.stdout.strip())

    def test_original_unchecked_cast_crashes_in_every_mode(self):
        for mode in ("batch", "round-robin", "sequential"):
            with self.subTest(mode=mode):
                result = self.run_case("unchecked", mode, "non-source")
                self.assertNotEqual(result.returncode, 0)
                self.assertIn("ClassCastException", result.stderr)
                print(f"CONFIRMED original {mode}: ClassCastException")

    def test_round_robin_normalizes_persisted_index(self):
        for index in (-1, -2147483648, 0, 5, 2147483647):
            with self.subTest(index=index):
                result = self.run_case("fixed", "index", str(index))
                self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
                print(result.stdout.strip())


if __name__ == "__main__":
    unittest.main(verbosity=2)
