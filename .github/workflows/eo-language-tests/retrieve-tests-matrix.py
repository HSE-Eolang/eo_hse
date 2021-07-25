import os, json

dirs = []

for dir in next(os.walk(".github/workflows/eo-language-tests/."))[1]:
  dirs.append(dir)

print("::set-output name=matrix::" + json.dumps(dirs))