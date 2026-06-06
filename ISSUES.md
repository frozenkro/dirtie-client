# dirtie-client Known Issues & Blockers

> Updated after Android app fix pass. Fixed items are marked [FIXED].

---

## Critical (Will Block E2E Test)

### 1. [FIXED] Date Parsing / Formatting Bugs
**Files:** `DeviceRepository.kt`, `DeviceService.kt`
**Problems:**
- `SimpleDateFormat` had typo `"yyy-MM-dd'T'HH:mm'Z'"` (missing a `y`) in temperature query.
- Format lacked seconds (`HH:mm` instead of `HH:mm:ss`), causing server RFC3339 parse to fail.
- Used deprecated `Calendar` / `SimpleDateFormat` APIs.
**Fix Applied:**
- Replaced with `DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")` + `OffsetDateTime.now(ZoneOffset.UTC).minusDays()`.
- Centralized in `buildStartTime()` helper.
- Added `runCatching { }` around per-device API calls so one failure doesn't blank the entire list.

### 2. [FIXED] Provisioning Token Response JSON Mismatch
**Files:** `DirtieSrvApi.kt`, `DeviceRepository.kt`
**Problem:** Android expected `Response<String>` but server returns `{"contract": "uuid"}`. Gson threw JsonSyntaxException.
**Fix Applied:** Added `ApiProvisioningToken(val contract: String)` data class. API now returns `Response<ApiProvisioningToken>`, repository extracts `.contract`.

### 3. [FIXED] DeviceAdapter Missing Temperature
**Files:** `DeviceAdapter.kt`, `item_device.xml`
**Problem:** List items only showed raw capacitance number. No temperature visible.
**Fix Applied:** Added `deviceTemp` TextView to layout. Adapter now shows formatted "Cap: X / Temp: Y°C".

### 4. [FIXED] Chart Fragment Used Mock Data
**Files:** `DeviceChartFragment.kt`, `fragment_device_chart.xml`
**Problem:** `refreshChartData()` called `generateMockReadings()` exclusively. `deviceId` from navArgs ignored. ViewModel bypassed. No loading/error states.
**Fix Applied:**
- Fragment now uses `by viewModel()` and `by navArgs()`.
- Observes `DeviceChartViewModel.state` (Loading / Success / Error).
- On Success, reads real capacitance/temperature from ViewModel state based on active tab.
- Added `ProgressBar` and `errorText` to layout.

### 5. [FIXED] Chart X-Axis Formatter Ignored Timestamps
**File:** `DeviceChartFragment.kt` — `DateAxisFormatter`
**Problem:** Returned `HH:mm` of `Instant.now()` for every label.
**Fix Applied:** Constructor now takes `List<Long>` of actual timestamps. `getFormattedValue()` maps index → real epoch-second → formatted time.

### 6. [FIXED] Provisioning POST Had No Retry
**File:** `WifiProvisioningManager.kt`
**Problem:** Single attempt, swallowed all exceptions, returned opaque false.
**Fix Applied:** `repeat(3)` with 2-second delay between attempts. Logs each failure with attempt number.

### 7. [FIXED] MainActivity Nav Controller Incomplete
**File:** `MainActivity.kt`
**Problem:** Only called `setContentView(R.layout.activity_main)`. No nav controller or AppBar setup.
**Fix Applied:** Uses ViewBinding, finds nav controller, sets AppBarConfiguration with `homeFragment` as top-level destination.

---

## Remaining Blockers (Require Server and/or Firmware Work)

### 8. Server `/devices` Handler Marshals Wrong Type
**File:** `dirtie-srv/internal/api/handlers/device_hnd.go:45`
**Problem:** `getUserDevicesHandler` marshals raw `[]sqlc.Device` (with `pgtype.Text` fields) instead of `dtoList`. Android Gson cannot deserialize nested `pgtype.Text` JSON into `ApiDevice`.
**Fix Needed (dirtie-srv):** Change `json.Marshal(devices)` → `json.Marshal(dtoList)`.

### 9. Server InfluxDB Query Missing Device Filter
**File:** `dirtie-srv/internal/db/influx.go:81-85`
**Problem:** `GetValuesRange` does not filter by `device` tag. Returns all devices' data for any request.
**Fix Needed (dirtie-srv):** Add `|> filter(fn: (r) => r.device == "<deviceId>")` to the Flux query.

### 10. Server `createProvision` Requires `displayName`
**File:** `dirtie-srv/internal/api/handlers/device_hnd.go:57-62`
**Problem:** Returns 400 BadRequest if `displayName` query param is absent. Android provisioning flow does not send one.
**Fix Needed (dirtie-srv):** Make `displayName` optional and default to "Dirtie Device" when absent.

### 11. dirtie-node MQTT / Provisioning Logic Is Stub
**Files:** `dirtie-node/src/connect/connect.c`, `dirtie-node/src/state/state.c`
**Problem:**
- `mqtt_init()` is a no-op (`return 0`).
- The Pico never connects to home Wi-Fi in station mode after receiving credentials.
- The Pico never publishes `{macAddr, contract}` to `dirtie-provision` topic.
- The Pico never reads real sensor data or publishes to `dirtie-breadcrumb`.
- State machine handlers (`mqtt_init_handler`, `mqtt_publish_handler`, `sense_listen_handler`) exist in `state.h` callbacks table but have no implementation files.
**Fix Needed (dirtie-node):**
- Implement `mqtt_init_handler`: connect Wi-Fi via `cyw43_arch_wifi_connect_timeout_ms`, initialize MQTT client with `hub_loc`.
- Implement `mqtt_publish_handler`: build JSON payload with `macAddr` + `prv_token`, publish to `dirtie-provision`.
- Implement sensor read + periodic publish to `dirtie-breadcrumb`.

---

## Code Quality / Tech Debt

### 12. [PARTIALLY FIXED] `DeviceChartViewModel` Flow Collection
**File:** `DeviceChartViewModel.kt`
**Status:** Was previously using blocking `collect()` on two flows sequentially. Fixed in earlier pass to use `.first()`. Current code is correct.

### 13. `AndroidManifest.xml` — Cleartext Traffic Enabled
**File:** `AndroidManifest.xml` line 10
**Problem:** `android:usesCleartextTraffic="true"` is set globally. Fine for local dev on `192.168.x.x`, but a security issue if the app ever targets a public domain.
**Suggested:** Move to a `network_security_config.xml` that only allows cleartext for the specific API IP.

### 14. `DeviceService.refreshDevices()` — No Device Tag Filter in Data
**File:** `DeviceService.kt`
**Problem:** The per-device data fetch works correctly for the Android side, but the server query (see Blocker #9) returns unfiltered data. If multiple devices exist, the Android app will show cross-device readings.
**Status:** Requires server fix.

### 15. BASE_URL Hardcoded
**File:** `ApiClient.kt`
**Problem:** `BASE_URL = "http://192.168.0.114/dirtie/"` is hardcoded. Inflexible for different environments.
**Suggested:** Move to `BuildConfig` field populated from Gradle build type or external config.

### 16. Navigation SafeArgs — Build Required
**Files:** `nav_graph.xml`, `home_nav_graph.xml`
**Problem:** Direction classes (`DeviceListFragmentDirections`, `DeviceDetailFragmentDirections`, `DeviceChartFragmentArgs`) are generated at build time by the SafeArgs plugin.
**Action:** Run `./gradlew build` or `./gradlew generateSafeArgs`. IDE red squiggles on direction classes will clear after generation.

---

## Next Steps (Recommended Order)

1. **[Android]** Run `./gradlew build` to generate SafeArgs and verify MPAndroidChart resolves.
2. **[Server]** Fix `/devices` handler to marshal `dtoList` instead of raw `sqlc.Device`.
3. **[Server]** Add device tag filter to InfluxDB Flux query in `GetValuesRange`.
4. **[Server]** Make `displayName` optional in `createDeviceProvision` handler.
5. **[Firmware]** Implement `mqtt_init_handler` and `mqtt_publish_handler` in dirtie-node.
6. **[Firmware]** Implement sensor read + `dirtie-breadcrumb` publish loop.
7. **[E2E Test]** Run full flow: user create → login → provision token → Pico AP connect → provision POST → Pico Wi-Fi connect → MQTT provision complete → sensor data → Android list + chart view.
