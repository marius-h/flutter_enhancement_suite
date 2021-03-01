package de.mariushoefler.flutterenhancementsuite.exceptions

class GetLatestPackageVersionException(p: String) :
    Exception("Cannot get the latest version number for package: $p")

class GetCurrentPackageVersionException(p: String) :
    Exception("Cannot read current version number for package: $p")

class GetPackageNameException(p: String) :
    Exception("Cannot read package name for package: $p")
