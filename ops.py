import os
import sys
import json
import subprocess as sbp
from statistics import median, mean


JAR = "aggregator-test-starter-0.0.2-SNAPSHOT-jar-with-dependencies.jar"
NODE = "worldcongress2017_1pilot_resologi_1odtf1_1ca_1qc_1mtl_1mobil_1traf_1detector_1det0_1det-00721-01_1lane2_1measure0_1car_185-per-speed"
AGGREG_TYPE = "MEAN"
TELEMETRY_LOC = 'telemetry.json'


def update_aggregators(data: dict) -> None:

    sorted_keys = [int(x) for x in data.keys()]
    data = {key: data[str(key)] for key in sorted_keys}

    if max(data.values()) == 0:
        print("No subscribers at all!")
        print("All working aggregators are kept")
        display_running_aggregators(data)
        return

    threshold = max(1, median(data.values()))

    # removes aggregators with a number of subscribers below 'threshold'
    new_data = {
        key: val
        for key, val in data.items() if val >= threshold
    }

    for i, current_key in enumerate(sorted_keys[1:]):
        previous_key = sorted_keys[i]

        # creates new aggregators if current and previous
        # aggregators have enough subscribers
        if min(data[current_key], data[previous_key]) >= threshold:
            # new_key is equal to the integer nearest the mean
            new_key = int(mean([current_key, previous_key]) + .5)
            if new_key not in data.keys() and new_key > 0:
                new_data[new_key] = 0

    min_key = sorted_keys[0]
    max_key = sorted_keys[-1]

    # creates new subscriber with a values equals to 80% to the current min
    # if the current min aggregator has enough subscribers
    if data[min_key] >= threshold:
        new_aggreg = 8 * min_key // 10
        if new_aggreg > 0:
            new_data[new_aggreg] = 0

    # creates new subscriber with a values equals to 120% to the current max
    # if the current max aggregator has enough subscribers
    if data[max_key] >= threshold:
        new_data[12 * max_key // 10] = 0

    # aggregators to start and to stop are deduces of the differences
    # between 'data' and 'new_data'
    aggreg_to_stop = set(data) - set(new_data)
    aggreg_to_start = set(new_data) - set(data)

    # ensures the aggregators are sorted before being saved
    new_data = {key: new_data[key] for key in sorted(new_data.keys())}

    # saves the new aggregators
    with open(TELEMETRY_LOC, 'w') as telemetry:
        json.dump(new_data, telemetry)

    print("Aggregators to be shutdown :", ", ".join(map(str, aggreg_to_stop)))
    for aggreg in aggreg_to_stop:
        sbp.run(f"echo \"java -jar {JAR} stop {aggreg}\"", shell=True)

    print("\nAggregators to be start :", ", ".join(map(str, aggreg_to_start)))
    for aggreg in aggreg_to_start:
        sbp.run(f"echo \"java -jar {JAR} start {aggreg} {AGGREG_TYPE} {aggreg} {NODE}\"", shell=True)

    print("\nAggregators successfully updated")
    print("New running aggregators : ", ", ".join(map(str, new_data)))


def subscribe_or_unsubscribe(data: dict, add: int) -> None:
    for topic in topics:
        if topic in data:
            data[topic] = max(data[topic] + add, 0)
            print(f"successfully {action} to topic: {topic}")
        else:
            print(f"The topic {topic} doesn't exist!")

    with open(TELEMETRY_LOC, 'w') as telemetry:
        json.dump(data, telemetry)


def display_running_aggregators(data: dict) -> None:
    print("List of currently available aggregators:")
    for item in data.items():
        print("-", " : ".join(map(str, item)))


if __name__ == "__main__":

    # 'action' contains the action to perform and topics the eventual specified topics
    action, *topics = sys.argv[1:]

    # loads the file that contains the telemetry of the aggregators
    with open(TELEMETRY_LOC) as telemetry:
        data = json.load(telemetry)

    # executes the correct operations depending on the action to perform
    if action == "update":
        update_aggregators(data)

    elif action in ["subscribe", "unsubscribe"]:
        add = 1 if action == "subscribe" else -1
        subscribe_or_unsubscribe(data, add)

    elif action == "list" :
        display_running_aggregators(data)

    else:
        print('Invalid specified command')
        sys.exit(-1)
