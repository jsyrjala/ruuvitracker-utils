ruuvitracker-utils
==================

Some utilities for [RuuviTracker](https://github.com/RuuviTracker/)

## NMEA Importer
[NMEA](http://wiki.openstreetmap.org/wiki/NMEA) importer for RuuviTracker server.

#### Usage

    cd import
    lein run -m ruuvitracker.import.importer NMEA-DATA RUUVITRACKER-API-URL TRACKER-ID SHARED-SECRET

Example:

    lein run -m ruuvitracker.import.importer http://vapiti.lnet.fi/nmea-auto-public.txt http://localhost:3000/api/v1-dev/events foobar foobar

