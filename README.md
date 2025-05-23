# EU Carrier Control

An Android application designed to help travelers maintain connectivity exclusively with EU carriers while traveling through different countries. This app helps prevent unexpected roaming charges by automatically managing carrier connections.

## Features

- 🔒 Automatic blocking of non-EU carriers
- 📱 Real-time carrier monitoring
- 🇪🇺 Comprehensive EU carrier database
- 📊 Current carrier status display
- 🔄 Easy toggle for protection service

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
5. The app will automatically run in the background once enabled

## Permissions Required

- `READ_PHONE_STATE`: For carrier detection
- `ACCESS_FINE_LOCATION`: For precise carrier location
- `ACCESS_COARSE_LOCATION`: For general network location
- `CHANGE_NETWORK_STATE`: For carrier management
- `ACCESS_NETWORK_STATE`: For network status monitoring

## Technical Details

- Built with Kotlin
- Uses Android's TelephonyManager API
- Implements background service for continuous monitoring
- Material Design 3 UI components
- Minimum SDK: 24 (Android 6.0)
- Target SDK: 34 (Android 14)

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Privacy Policy

This application does not collect or transmit any personal data. All carrier management is performed locally on your device.
