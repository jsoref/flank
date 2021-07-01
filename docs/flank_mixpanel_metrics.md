# Mixpanel events in Flank

Flank is currently tracking the following events:

- configuration
- bundle id and package name with information about the device type
- total cost
- cost per device type
- test duration
- flank version with information if tests run on corellium or firebase

Every event contains a session id and project id. By ```project id``` you can find all events from every execution with specific ```project id```. By ```session id``` you can find events from specific Flank execution.

## Configuration

This event contains information about the configuration executed in Flank. This event allows us to
track information about devices and features used in Flank.
It could be useful to check what features are most important for the community.

Fields reflect configuration names.

## Bundle and package id [app_id]

This event contains information about the bundle id for ios and the package id for android project. Additionally,
contains platform type (android, ios). This event allows us to implement a ```Who uses Flank``` report with additional breakdown by ```device type```.

Fields:

- ```app_id```
- ```device_type```

## Total cost and cost per device type [devices_cost]

With these event's we can realize the report ```How many millions per month in spend is Flank responsible for on Firebase Test Lab? ```

Fields:

- ```virtual_cost```
- ```physical_cost```
- ```total_cost```

## Test duration [total_test_time]

With these event's we can check how long tests took.

Fields:

- ```test_duration```

## Flank Version [flank_version]

By these event's we can check how frequently users upgrade the Flank version.

Fields:

- ```version```
- ```test_platform```