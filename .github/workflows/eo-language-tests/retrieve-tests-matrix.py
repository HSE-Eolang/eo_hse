import os, json

path = ".github/workflows/eo-language-tests/"
tests = []

for dir in next(os.walk(path + "."))[1]:
  with open(path + dir + "/test.json", 'r') as test_data_file:
    test_data = json.load(test_data_file)
  
  test_data["directory"] = str(dir)
  tests.append(test_data)

print("::set-output name=matrix::" + json.dumps(test_data))