# ProxyPortal

ProxyPortal is a mod designed for Fabric servers that uses Velocity as a proxy server, allowing admin to create portal for quickly teleport between different servers.

## System Requirements

**Proxy Server**: Velocity (for cross-server functionality)

## Features

- **Custom Portals**: Create portals to teleport players between servers.
- **Native Portal Override**: Automatically cancels native teleportation for Nether Portals, End Portals, and End Gateways.

## Usage

### Permission Requirements

All ProxyPortal commands require **administrator permissions** (permission level 2 or above).

### Create Portal
```
/portal create <name> <x1> <y1> <z1> <x2> <y2> <z2> <target_server>
```

- `<name>`: Unique name for the portal
- `<x1> <y1> <z1>`: First corner coordinates of the portal area
- `<x2> <y2> <z2>`: Second corner coordinates of the portal area
- `<target_server>`: Target server name (as configured in Velocity)

**Example:**
```
/portal create spawn_portal 100 64 200 105 70 205 lobby
```
This creates a portal named "spawn_portal" with area from coordinates (100,64,200) to (105,70,205), targeting the "lobby" server.

### Delete Portal
```
/portal delete <name>
```

### List All Portals
```
/portal list
```

### Reload Portal Data
```
/portal reload
```

### Portal Appearance

ProxyPortal portals are invisible regions and do not generate any blocks or structures automatically.
Admins are encouraged to decorate portals with blocks, lights, or particle effects to make them visually recognizable to players.

### Native Portal Override

ProxyPortal automatically cancels the native teleportation behavior of the following portals:
- **Nether Portals**
- **End Portals**
- **End Gateways**

This ensures that players do not accidentally trigger vanilla teleportation mechanics, allowing server admins to fully control teleportation behavior through ProxyPortal.
