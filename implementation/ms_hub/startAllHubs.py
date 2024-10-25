import json
import os

jar_path = 'build/libs/ms_hub.jar'
ms_type = 'hub'

with open('../config.json', 'r') as file:
    config = json.load(file)

uuids = []

for uuid, value in config.items():
    if value['nodeType'] == ms_type:
        uuids.append(uuid)


for uuid in uuids:
    os.system(f"start cmd /k java -jar {jar_path} {uuid}")

