# Technical Specification: Dirtie Client Android App

## 1. Objectives
The Dirtie Client is a utility application designed to manage IoT soil sensors. Its primary goals are:
1. **Authentication**: Secure user access to the Dirtie ecosystem.
2. **Monitoring**: Visualizing soil moisture (capacitance) and temperature data.
3. **Provisioning**: Configuring new IoT devices with local network credentials and server-side registration tokens.

---

## 2. System Architecture
- **Android App**: Kotlin, ViewModel, Repository pattern, Retrofit for networking.
- **Web Server**: Central authority for user accounts, device registration, and telemetry storage.
- **IoT Device**: ESP32 or similar, acting as a WiFi Access Point (AP) during setup and a station (STA) during operation.

---

## 3. Functional Specifications

### 3.1 Authentication & User Management
- **Registration**: Create a new account via `/users`.
- **Login**: Authenticate and establish a session via `/login`.
- **Session Management**: Use `PersistentCookieStore` to maintain authenticated state across app restarts.
- **Password Recovery**: Trigger a reset via `/pw/reset`.

### 3.2 Device Monitoring
- **Device Discovery**: Fetch a list of devices associated with the authenticated user via `/devices`.
- **Telemetry Retrieval**: 
    - Fetch capacitance data via `/data/capacitance`.
    - Fetch temperature data via `/data/temperature`.
    - **Filter**: Data is requested based on `deviceId` and a `startTime` (ISO 8601 UTC).

### 3.3 Device Provisioning (The "Handshake")
The provisioning flow allows a device to transition from "Factory State" to "Operational State."

**The Workflow:**
1. **Token Acquisition**: App requests a unique `provisioningToken` from the server via `/devices/createProvision`.
2. **AP Connection**: App prompts the user to connect to the device's setup WiFi (SSID: `Dirtie-XXXX`).
3. **Configuration Push**:
   - App binds process to the device network.
   - App sends a POST request to `http://192.168.4.1/provision`.
   - **Payload**: `{ "ssid": "HomeWiFi", "password": "Password123", "token": "TOKEN_FROM_SERVER" }`.
4. **Transition**: Device saves credentials, reboots, and connects to `HomeWiFi` to register itself with the server using the token.

---

## 4. API Reference

### 4.1 Server API (`DirtieSrvApi`)
| Endpoint | Method | Payload/Params | Response | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/login` | POST | `ApiLoginRequest` | `Unit` (Cookie) | Authenticates user |
| `/logout` | POST | N/A | `Unit` | Ends session |
| `/users` | POST | `ApiCreateUserArgs` | `ApiUser` | Registers new user |
| `/pw/reset` | POST | `email: String` | `Unit` | Requests password reset |
| `/devices` | GET | N/A | `List<ApiDevice>` | Lists user's devices |
| `/devices/createProvision` | POST | N/A | `String` | Returns a one-time token |
| `/data/capacitance` | GET | `deviceId`, `startTime` | `List<DataPoint>`| Fetches moisture data |
| `/data/temperature` | GET | `deviceId`, `startTime` | `List<DataPoint>`| Fetches temp data |

### 4.2 Local Device API
| Endpoint | Method | Payload | Response | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/provision` | POST | `{ssid, password, token}` | `200 OK` | Configures device WiFi/Auth |

---

## 5. Technical Constraints & Requirements
- **Permissions**: `ACCESS_FINE_LOCATION` is mandatory for WiFi scanning/connection on Android 10+.
- **Network Bind**: Must use `connectivityManager.bindProcessToNetwork()` to ensure HTTP requests reach the device AP instead of attempting to use the mobile data/home WiFi.
- **Timeouts**: Local provisioning requests should have extended timeouts (e.g., 30s) to account for device AP latency.
