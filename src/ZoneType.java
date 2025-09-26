//************************************************************
//  Authors: Garrett Reihner, Kaitlyn Cavanaugh
//  ZoneType.java
//
//  This is a custom enum for different zone types. To
//  avoid expensive string manipulation and equivalence
//  checking when matching zones for the visualization or
//  path finding, this custom enum has been implemented
//  to avoid that computational pitfall. It also provides
//  additional type safety when designing the system to
//  avoid typos that cause errors.
//************************************************************

public enum ZoneType {
    HOTSPOT,
    TERMINAL,
    AERODROME,
    PROPERTY_LINE
}
