Feature: New CFS is received on ODACA

  Scenario: managing new active CFS event from Event BUS
    Given a new Event is ServiceSpecificationStateChange
    When lifecycleStatus is active
    Then the event is saved in ODACA database as a CFS and ODACA sends an event to the event BUS containing this new CFS information