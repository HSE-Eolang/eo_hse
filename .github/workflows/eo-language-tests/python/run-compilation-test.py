import sys, os, shutil, subprocess
import json
import glob

test_dir = sys.argv[1]
path = ".github/workflows/eo-language-tests/tests/"


with open(path + test_dir + "/test.json", 'r') as test_data_file:
  test_data = json.load(test_data_file)


if test_data["type"] == "compilation":
  dest_dir = ".github/workflows/eo-language-tests/environments/compilation-tests-environment"
else:
  raise TypeError("Uknown type of the test!")

for filename in glob.glob(os.path.join(path + test_dir, '*.*')):
  if os.path.isfile(filename):
    shutil.copy(filename, dest_dir + "/eo")

subprocess.run(["ls", "-la", ".github/workflows/eo-language-tests/environments/compilation-tests-environment/eo"])

completed_compilation_process = subprocess.run(["mvn", "clean", "compile", "--file", dest_dir + '/pom.xml'])

if completed_compilation_process.returncode == 0:
  actual_compilation_result = "ok"
else:
  actual_compilation_result = "fail"

expected_compilation_result = test_data["result"]

if expected_compilation_result != actual_compilation_result:
  raise AssertionError("Expected compilation result: \"" + str(expected_compilation_result) + "\", but got: \"" + str(actual_compilation_result) + "\".")
else:
  print("OK!")
