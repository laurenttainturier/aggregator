import sys
import json
import subprocess as sbp

JAR = "aggregator-test-starter-0.0.2-SNAPSHOT-jar-with-dependencies.jar"
NODE = "<node>"
AGGREG_TYPE = "MEAN"


if __name__ == "__main__":
    with open('telemetry.json') as telemetry:
        data = json.load(telemetry)

    content, *topics = sys.argv[1:]

    if content == "update":
        new_data = {
            key: val
            for key, val in data.items() if val > 0
        }
        new_data[20] = 4

        aggreg_to_be_shutdown = set(data) - set(new_data)
        aggreg_to_be_start = set(new_data) - set(data)

        for aggreg in aggreg_to_be_shutdown:
            sbp.run(f"echo \"java -jar {JAR} stop {aggreg}\"", shell=True)

        for aggreg in aggreg_to_be_start:
            sbp.run(f"echo \"java -jar {JAR} start {aggreg} {AGGREG_TYPE} {aggreg} {NODE}\"", shell=True)
        
        print("Aggregator successfully updated")
        
    elif content in ["subscribe", "unsubscribe"]:
        add = 1 if content == "subscribe" else -1

        for topic in topics:
            if topic in data:
                data[topic] = max(data[topic] + add, 0)
                print(f"successfully {content} to topic: {topic}")
            else:
                print(f"The topic {topic} doesn't exist!")
        
    else:
        print('Invalid specified command')
        sys.exit(-1)

    with open('telemetry.json', 'w') as telemetry:
        json.dump(data, telemetry)

    print(data)
    
