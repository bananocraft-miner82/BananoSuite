# BananoSuite

Repo for BananoSuite, a bukkit plugin for Minecraft.

## Features

BananoSuite provides a number of commands and features for Bananocraft and is
works with Vault-enabled economy plugins to provide income-generating tools!

This plugin is a free software licenced under the GNU GENERAL PUBLIC LICENSE
Version 3.

## Commands and Permissions

### `/donate [amount]`

Used to make a donation to the server funds.

- OP users can add an extra parameter 'testmode' to test the command without
  initiating a transaction.
- Deducts the specified amount from the player's balance and sends it to the
  server's balance.

### `/rain [amount] [optional:nearby|everyone] [optional(nearby only):distance]`

Used to airdrop an amount to the other online players on the server.

- The airdropped amount will be divided up evenly amongst all of the
  online players on the server.
- The command will also trigger a short storm.
- aliases: [makeitrain]

### `/sethome`

Used to set player's home coordinates for use with the `/home` and `/teleport home` commands.

- Saves the player's current location as their home coordinates.
- If unspecified, the playérs home coordinates will be their last bed spawn location
  set when the plugin was first activated.
- The coordinates must be in the Overworld.

### `/teleport [HOME/SPAWN] [Optional:QUOTE]`

Used to teleport the player to their previously defined home coordinates.

- The player will be teleported to their home or spawn coordinates, for a fee.
- The further the teleport, the greater the fee, between a base specified amount and a
  maximum, specified in the configuration file.
- Adding the QUOTE parameter will provide the cost of the teleport
  from the player's current location. No teleport will occur and no
  funds will be deducted from the player's balance when passing the
  QUOTE parameter.
- Alias[es] `/tp`

### `/home [Optional:QUOTE]`

Used to teleport the player to their previously defined home coordinates.

- The player will be teleported to their home coordinates, for a fee.
- The further the teleport, the greater the fee, between a base specified amount and a
  maximum, specified in the configuration file.
- Adding the QUOTE parameter will provide the cost of the teleport 
  from the player's current location. No teleport will occur and no
  funds will be deducted from the player's balance when passing the
  QUOTE parameter.
- Alias[es] `/tph`

### `/spawn [Optional:QUOTE]`

Used to teleport the player to the world spawn coordinates.

- The player will be teleported to the world spawn coordinates, for a fee.
- The further the teleport, the greater the fee, between a base specified amount and a
  maximum, specified in the configuration file.
- Adding the QUOTE parameter will provide the cost of the teleport
  from the player's current location. No teleport will occur and no
  funds will be deducted from the player's balance when passing the
  QUOTE parameter.
- Alias[es] `/tps`

### `/deathinsurance [start/stop/quote/query] [start/quote only: None/Inventory/Full]`

Used to activate a Death Insurance policy.

- Activates/stops/queries/quotes a Death Insurance policy.
- Passing the parameters 'start' and a policy level of 'Inventory'
  or 'Full' will open a new policy, or upgrade an existing policy.
- When a Death Insurance policy is active and the player dies with
  sufficient funds to cover the policy premium, the amount will
  be deducted from their balance, and they will respawn with
  their inventory intact if using an Inventory policy, plus 
  xp if they have Full coverage.
- If the player has insufficient funds to cover the premium upon
  death, the player's items/xp will drop as a normal death and
  the player will respawn at their respawn point as normal.
- The Death Insurance premium will increase with each death
  within a 24 hour period.
- Passing the parameter 'stop' will terminate any existing policy.
- Passing the parameter 'quote' and a policy level will provide
  a message identifying the first premium to the requester.
- Passing the parameter 'query' will provide a message identifying
  the player's current policy level, if any.
- Already having an active policy and re-calling the command
  with a different policy option will override the existing
  policy with the new type.
- Alias[es]: `/di`

### `/pvptoggle [PVPOFF/PVPON/QUERY]`

Used to provide an estimate of the first Death Insurance policy premium.

- Parameter PVPOFF will opt the player out from PvP.
- Parameter PVPON will opt the player in for PvP.
- Parameter QUERY will give the player a message to identify
  their current PvP status.
- If a player is attacked with PvP opted-out, the attacker
  will take minor damage and the player being attacked will
  not incur any damage.

### `/monkeymap buy [MonKey|QRCode|help] [ban_ address] [MonKey:frame option|QRCode:text to display at the base of the QR code]`
### `/monkeymap query`
### `/monkeymap collect`
### `/monkeymap retry`
### `/monkeymap help`

Enables the player to buy a map displaying their MonKey, or a QR Code map, for their
provided Banano address.

- To query the prices, use the command `/buymonkeymap help`
- To query the status of any outstanding monkeymap orders, use the command `/monkeymap query`
- To collect any ready monkeymap orders, use `/monkeymap collect`
- To re-commence processing for any failed monkeymaps, use `/monkeymap retry`

MonKeys:
- MonKey commands follow the format:
  `/monkeymap MonKey ban_address [frame type:fancy|none] [Optional: text to use in place of the player's name]`
- If a frame is specified, it will be overlaid on the MonKey image.
- If no frame/none is specified, the MonKey will have the default background.

QR Codes:
- QR Code commands follow the format:
   `/monkeymap QRCode ban_address [Optional:Text to display at the bottom of the QR Code and for the map name]`
- If no additional text is defined, the purchasing player's name will be
  displayed at the base of the QR Code.

### OP-Only Commands

### `/enabledeathinsurance [true/false]`

Used to switch the death insurance feature on or off.

- Switches on Death Insurance
- Alias[es]: `/toggledeathpolicy`
- Permission: `bananosuite.setconfig`

### `/bananosuite reloadconfig`

Refreshes the configuration from the file saved in the plugin's data folder.

### `/bananosuite [deathinsurance|donate|monkeymaps|pvptoggle|rain|teleport] [enable|disable|query]`

Used to enable, disable or query the status of the specified command.

### `/raiseshields`

Used to make the OP players impervious to damage and facilitate unhindered moderation.

- Alias[es] `/redalert, /shieldsup`
- Permission: OP

### About the permissions

All permissions are by default granted to OP and any players with the permission `bananominer.setconfig`
set in the permissions file.

## Configuration

```yaml
mongoURI: "INSERT MONGOURI HERE"

EnableBananoSuite: true

# Home Command Configuration
RestrictHomeToOverworld: true

# Spawn Teleport Configuration
FreeSpawnTeleport: false
TeleportBasePremium: 1.9
TeleportMaximumPremium: 1000
TeleportGrowthRate: 0.1

# Death Insurance Configuration
EnableDeathInsurance: true
DeathInsuranceBasePremium: 19
DeathInsuranceMaximumPremium: 1900
DeathInsuranceGrowthRate: 0.19

#Donation Command Configuration
DonationGiftFireworks: true
DonationRandomGift: true
DonationClassifications:
  LOW:
    Threshold: 1
  MEDIUM:
    Threshold: 19
  HIGH:
    Threshold: 69
  ULTIMATE:
    Threshold: 690

DonationPrizes:
  YELLOW_DYE:
    Classification: "LOW"
    Weighting: 1
    Minimum: 1
    Maximum: 3
  YELLOW_CONCRETE:
    Classification: "MEDIUM"
    Weighting: 1
    Minimum: 1
    Maximum: 5
  GOLDEN_PICKAXE:
    Classification: "HIGH"
    Weighting: 1
    Minimum: 1
    Maximum: 1
  YELLOW_SHULKER_BOX:
    Classification: "ULTIMATE"
    Weighting: 4
    Minimum: 1
    Maximum: 1
```

## Changelog

### v1.0.0 — First Release

- Core functionality and commands, including:
- Commands: Donate, Death Insurance, Home/Spawn Teleport, PvP Opt-In/Out, Rain, RaiseShields, ReloadConfig.

### v1.1.0 - Next release

- To be decided!