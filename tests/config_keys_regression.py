"""Run with python tests/config_keys_regression.py (Python 3 and JDK 17+).

Executes the real MaidConfigKeys with minimal dependency doubles. Unknown/invalid
network keys must be ignored; registered keys must preserve their value types.
"""

from pathlib import Path
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "src/main/java/com/chiyukiruon/maid_mana_source/data/MaidConfigKeys.java"
STUBS = {
    "org.jetbrains.annotations.Nullable": "public @interface Nullable {}",
    "net.minecraft.resources.ResourceLocation": "public record ResourceLocation(String name) {}",
    "com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey":
        "public interface TaskDataKey<T> {}",
    "com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid": """
        import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
        public class EntityMaid {
            public int reads;
            public <T> T getOrCreateData(TaskDataKey<T> key, T defaultValue) {
                reads++;
                return defaultValue;
            }
        }
    """,
}
HARNESS = """
import com.chiyukiruon.maid_mana_source.data.MaidConfigKeys;
import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.resources.ResourceLocation;
public class ConfigKeysRegression {
    static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        EntityMaid maid = new EntityMaid();
        if (args[0].equals("unknown")) {
            check(MaidConfigKeys.getValue(maid, new ResourceLocation("unknown:key")) == null, "unknown key");
            check(maid.reads == 0, "unknown key must not access maid data");
        } else if (args[0].equals("invalid")) {
            // ResourceLocation.tryParse returns null for an invalid packet key.
            check(MaidConfigKeys.getValue(maid, null) == null, "invalid key");
            check(maid.reads == 0, "invalid key must not access maid data");
        } else {
            ResourceLocation text = new ResourceLocation("test:text"), number = new ResourceLocation("test:number");
            MaidConfigKeys.addKey(text, new TaskDataKey<String>() {}, () -> "value");
            MaidConfigKeys.addKey(number, new TaskDataKey<Integer>() {}, () -> 42);
            check("value".equals(MaidConfigKeys.getValue(maid, text)), "string registration");
            check(Integer.valueOf(42).equals(MaidConfigKeys.getValue(maid, number)), "integer registration");
            check(maid.reads == 2, "each registered key must access maid data once");
        }
        System.out.println("PASS " + args[0]);
    }
}
"""


class ConfigKeysRegressionTest(unittest.TestCase):
    def test_config_key_lookup(self):
        with tempfile.TemporaryDirectory(prefix="config-regression-") as directory:
            base = Path(directory)
            files = [SOURCE]
            for name, body in STUBS.items():
                package, _, simple = name.rpartition(".")
                path = base / Path(*package.split(".")) / (simple + ".java")
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_text(f"package {package};\n{body}", encoding="utf-8")
                files.append(path)
            harness = base / "ConfigKeysRegression.java"
            harness.write_text(HARNESS, encoding="utf-8")
            files.append(harness)
            compile_result = subprocess.run(
                ["javac", "--release", "17", "-Xlint:unchecked", "-Werror", "-encoding", "UTF-8",
                 "-d", str(base / "classes"), *map(str, files)], capture_output=True, text=True,
            )
            self.assertEqual(compile_result.returncode, 0, compile_result.stdout + compile_result.stderr)
            for scenario in ("unknown", "invalid", "registered"):
                with self.subTest(scenario=scenario):
                    result = subprocess.run(
                        ["java", "-cp", str(base / "classes"), "ConfigKeysRegression", scenario],
                        capture_output=True, text=True,
                    )
                    self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
                    print(result.stdout.strip())


if __name__ == "__main__":
    unittest.main(verbosity=2)
