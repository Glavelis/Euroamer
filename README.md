# EU Carrier Control

An Android application designed to help travelers maintain connectivity exclusively with EU carriers while traveling through different countries. This app helps prevent unexpected roaming charges by automatically managing carrier connections.

## Features

- ⚡ Automatic blocking of non-EU carriers
- 📡 Real-time carrier monitoring
- 🌍🌍 Comprehensive EU carrier database
- 📊 Current carrier status display
- ⚙️ Easy toggle for protection service
- 🗺️ OpenStreetMap integration showing vehicle location
- 🟢 Green overlay when vehicle is within EU countries
- 📍 Real-time location tracking with EU boundary detection
- 🌙 Dark theme for comfortable night usage
- 🌎 Current country display based on location
- 🔍 150-meter default zoom level for precise location view
- ✨ Improved text visibility with white text on dark background
- 🌏 **NEW**: Accurate country detection using carrier MCC codes
- 📍 **NEW**: Fixed country detection for Germany (MCC 262)

## Supported EU Countries

The application supports all EU member states and their respective mobile carriers, including:
- Austria, Belgium, Bulgaria, Croatia, Cyprus
- Czech Republic, Denmark, Estonia, Finland, France
- Germany, Greece, Hungary, Ireland, Italy
- Latvia, Lithuania, Luxembourg, Malta, Netherlands
- Poland, Portugal, Romania, Slovakia, Slovenia
- Spain, Sweden

## Requirements

- Android 6.0 (API level 24) or higher
- Location services enabled
- Phone state permissions granted
- Internet connection for map tiles

## Installation

1. Download the APK from the releases section
2. Enable "Install from Unknown Sources" in your Android settings
3. Open the downloaded APK and follow installation prompts
4. Grant all requested permissions when prompted

## Usage

1. Launch the application
2. Grant necessary permissions when prompted
3. Toggle "Enable EU Carrier Protection" to start the service
4. Use "Check Current Carrier" to view your current network status
5. View your location on the integrated map
6. Green overlay indicates you're within EU boundaries
7. The app will automatically run in the background once enabled

## Permissions Required

- `READ_PHONE_STATE`: For carrier detection
- `ACCESS_FINE_LOCATION`: For precise carrier location
- `ACCESS_COARSE_LOCATION`: For general network location
- `CHANGE_NETWORK_STATE`: For carrier management
- `ACCESS_NETWORK_STATE`: For network status monitoring
- `INTERNET`: For map tile downloads
- `WRITE_EXTERNAL_STORAGE`: For map cache storage

## Technical Details

- Built with Kotlin
- Uses Android's TelephonyManager API
- Implements background service for continuous monitoring
- Material Design 3 UI components
- OpenStreetMap integration with osmdroid library
- Real-time location tracking and EU boundary detection
- Minimum SDK: 24 (Android 6.0)
- Target SDK: 34 (Android 14)

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Privacy Policy

This application does not collect or transmit any personal data. All carrier management is performed locally on your device. Location data is used only for local EU boundary detection and is not transmitted to external servers.