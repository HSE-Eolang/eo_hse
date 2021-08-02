import sys, os, shutil, subprocess
import json
import glob

test_dir = sys.argv[1]
path = ".github/workflows/eo-language-tests/tests/"


with open(path + test_dir + "/test.json", 'r') as test_data_file:
  test_data = json.load(test_data_file)


if test_data["type"] == "runtime":
  dest_dir = ".github/workflows/eo-language-tests/environments/runtime-tests-environment"
else:
  raise TypeError("Uknown type of the test!")

for filename in glob.glob(os.path.join(path + test_dir, '*.*')):
  if os.path.isfile(filename):
    shutil.copy(filename, dest_dir + "/eo")

subprocess.run(["ls", "-la", ".github/workflows/eo-language-tests/environments/runtime-tests-environment/eo"])

completed_compilation_process = subprocess.run(["mvn", "clean", "compile", "--file", dest_dir + '/pom.xml'])

if not completed_compilation_process.returncode == 0:
  raise AssertionError("This is a runtime test. Although the compilation of the test's sources failed!")

completed_runtime_process = subprocess.run([dest_dir + "/run.sh"], text=True, capture_output=True, encoding="utf-8")

expected_result = test_data["result"]

if completed_runtime_process.returncode == 0 and expected_result == "_fail":
  raise AssertionError("This runtime test must have failed, although it run with no exceptions.")
elif completed_runtime_process.returncode != 0 and expected_result != "_fail":
  raise AssertionError("This runtime test must have run with no exception, however it failed.")
elif expected_result == "_ok":
  print("OK!")
else:
  real_result = completed_runtime_process.stdout

  if expected_result != real_result:
    raise AssertionError("Expected runtime result: \"" + str(expected_result) + "\", but got: \"" + str(real_result) + "\".")
  else:
    print("OK!")
