# Changelog

All notable changes to this project will be documented in this file.

## [1.2] - 2025-01-14

### Added
- 🗺️ OpenStreetMap integration showing real-time vehicle location
- 🟢 Green overlay polygons highlighting EU countries on map
- 📍 Real-time location tracking with GPS and network providers
- 🌍 EU boundary detection using simplified country polygons
- 📱 Enhanced split-screen UI layout with map and controls
- 🔧 Modular architecture with EUBoundaryChecker and MapOverlayManager
- 🧪 Unit tests for EU boundary detection functionality
- 📋 Updated permissions for map functionality (INTERNET, WRITE_EXTERNAL_STORAGE)

### Changed
- Updated app version to 1.2 (versionCode 3)
- Enhanced README with new features and requirements
- Improved UI layout with CardView containers

### Dependencies
- Added osmdroid library (6.1.17) for OpenStreetMap integration
- Added Google Play Services Location (21.0.1) for location services

## [1.1] - Previous Release

### Added
- ⚡ Automatic blocking of non-EU carriers
- 📡 Real-time carrier monitoring
- 🌍 Comprehensive EU carrier database
- 📊 Current carrier status display
- ⚙️ Easy toggle for protection service
- 🔧 Background service for continuous monitoring
- 📱 Material Design 3 UI components

### Technical
- Built with Kotlin
- Uses Android's TelephonyManager API
- Minimum SDK: 24 (Android 6.0)
- Target SDK: 34 (Android 14)