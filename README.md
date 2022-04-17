# IV-EV-Checker
- A very simple mod that allows you to give out an item that has limited usage and displays a pokemons IV's and EV's on use.
- Requires Pixelmon Reforged or Pixelmon Generations

# 1.Commands and permissions
## 1.1 Commands 
- [] = optional <> = required
- /ivchecker give <username> [isUnlimited] (Gives specified username aka player the item. isUnlimited is a boolean flag in which case the item will never run out of uses)
## 1.2 Permissions 
- ivevchecker.command.ivevchecker (Main command)
# 2.Config
    # Define the item you want to use [default: minecraft:stick]
    S:item=minecraft:stick

    # Define the name of the item [default: &4IV/EV Checker]
    S:itemName=&4IV/EV Checker

    # Set's the maximum usage of defined item [range: 1 ~ 2147483647, default: 10]
    I:maxUsage=10

    # Should the item only work on pokemon that are owned? [default: false]
    B:onlyWorkOnOwnedPokemon=false
# Notes
- Changing the Item in the config and restarting the server will make all previous items given out (assuming the item is actually different) invalid. 
- This mod is server side only. It won't work on client side.
# Selling via shopkeeper
- If you want to sell this item via a pixelmon shopkeeper you will need to edit the shopItems.json and add this in
    
      "id": "ivevchecker",
      "name": "minecraft:stick",
      "nbtData": "{display:{Name:\"§4IV/EV Checker\",Lore:[\"§5Usages left: §f10\"]}, ivevusage:10}",
      "itemData": 0,
      "buy": 5,
      "sell": 2
- Replacing the name with the config value that you set in the config and Name:\"§4IV/EV Checker with the itemName value, §5Usages left: §f10 (replacing the §f10 with §fmaxusagevalue (config value) and ivevusage:10 with ivevusage:maxUsage and setting every other value as you'd like.
- Then add it to whatever shop keeper you desire by specifying the item name like so:
    
      "name": "ivevchecker"
