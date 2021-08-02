import os, json

path = ".github/workflows/eo-language-tests/tests/"
tests = []

for dir in next(os.walk(path + "."))[1]:
  with open(path + dir + "/test.json", 'r') as test_data_file:
    test_data = json.load(test_data_file)
  
  test_data["directory"] = str(dir)

  if (test_data["type"] == "compilation" and test_data["active"]):
    tests.append(test_data)

matrix = {}
matrix["include"] = tests

print("::set-output name=matrix::" + json.dumps(matrix))