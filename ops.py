import sys
import json
import subprocess as sbp
import os

JAR = "aggregator-test-starter-0.0.2-SNAPSHOT-jar-with-dependencies.jar"
NODE = "<node>"
AGGREG_TYPE = "MEAN"

# command java -jar aggregator-test-starter-0.0.2-SNAPSHOT-jar-with-dependencies.jar start 
# "5 min 30 worldcongress2017/pilot_resologi/odtf1/ca/qc/mtl/mobil/traf/detector/det0/det-00721-01/lane2/measure0/car/85-per-speed"
if __name__ == "__main__":
    with open('telemetry.json') as telemetry:
        data = json.load(telemetry)


    content, *topics = sys.argv[1:]

    if content == "update":
        new_data = {
            key: val
            for key, val in data.items() if val > 0
        }
        key_t = 0
        val_t = 0

        total = 0 #number of all launched aggregators
        for key in enumerate(data.items()):
            total += int(key[1][1])

        for i,key in enumerate(data.items()):
            if i == 0: #first aggregator
                if key[1] > 0:
                    lower = int(0.8*int(key[0]))
                    if(lower >= 1):
                        new_data[str(lower)] = 0
            elif i == (len(data)-1): #last aggregator
                if key[1] > 0 :
                    upper = int(1.2*int(key[0]))
                    new_data[str(upper)] = 0
            else:   #others
                if int(key[1]) / total >= 0.1 and val_t > 0:
                    new_data[str(int(((int(key[0])+key_t)/2)))] = 0

            #previous aggregators values
            key_t = int(key[0])
            val_t = key[1]

        with open('telemetry.json', 'w') as telemetry:
            json.dump(new_data, telemetry)

        aggreg_to_be_shutdown = set(data) - set(new_data)
        aggreg_to_be_start = set(new_data) - set(data)

        print("aggregators to be shutdown : ")
        for aggreg in aggreg_to_be_shutdown:
            sbp.run(f"echo \"java -jar {JAR} stop {aggreg}\"", shell=True)

        print("aggregators to be start : ")
        for aggreg in aggreg_to_be_start:
            sbp.run(f"echo \"java -jar {JAR} start {aggreg} {AGGREG_TYPE} {aggreg} {NODE}\"", shell=True)

        
        print("Aggregator successfully updated")
        print("New aggregators : ")
        print(new_data)
        
    elif content in ["subscribe", "unsubscribe"]:
        add = 1 if content == "subscribe" else -1

        for topic in topics:
            if topic in data:
                data[topic] = max(data[topic] + add, 0)
                print(f"successfully {content} to topic: {topic}")
            else:
                print(f"The topic {topic} doesn't exist!")

        with open('telemetry.json', 'w') as telemetry:
            json.dump(data, telemetry)

    elif content == "list" :
        print("List of currently availiable aggregators ('id': value) : ")
        print(data)
    else:
        print('Invalid specified command')
        sys.exit(-1)
