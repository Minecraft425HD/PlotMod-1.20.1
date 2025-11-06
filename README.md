# PlotMod-1.20.1
# PlotMod-1.20.1

*A full featured Plot system for Minecraft 1.20.1/Forge 47.5.0*

---

## Features / Funktionen

**English**

- Create, buy, sell, and manage plots using a selection tool or commands.
- Trust and untrust players for cooperative building.
- List, info, and rate plots.
- Rent plots, extend, cancel, abandon, or transfer plots.
- Trusted player access and management.
- Daily reward system for regular players.
- Shop system for buying and selling items.
- All commands are available in-game and can be restricted by permissions.

**Deutsch**

- Erstellen, kaufen, verkaufen und verwalten von Grundstücken per Selection Tool oder Kommandos.
- Spieler vertrauen/entfernen für gemeinsames Bauen.
- Auflisten, Infos und Bewertung von Plots.
- Plots vermieten, verlängern, abbrechen, aufgeben oder übertragen.
- Verwaltung der vertrauten Spieler.
- Tägliches Belohnungssystem für aktive Spieler.
- Shopsystem zum Kaufen und Verkaufen von Items.
- Alle Befehle sind im Spiel verfügbar und können durch Berechtigungen eingeschränkt werden.

---

## Commands / Kommandos

### Plot Commands / Plot-Befehle

| Command (English)                   | Kommando (Deutsch)                                         | Description (ENGLISH)                        | Beschreibung (DEUTSCH)                         |
|-------------------------------------|------------------------------------------------------------|----------------------------------------------|------------------------------------------------|
| `/plot wand`                        | `/plot wand`                                               | Gives the selection tool                     | Gibt das Selection Tool                       |
| `/plot create <price>`              | `/plot create <preis>`                                     | Create a plot from the selected region       | Erstellt ein Grundstück aus der Selektion      |
| `/plot buy`                         | `/plot buy`                                                | Buy the current plot                         | Kauft das aktuelle Grundstück                  |
| `/plot list`                        | `/plot list`                                               | List all plots                               | Alle Grundstücke auflisten                     |
| `/plot info`                        | `/plot info`                                               | Show plot info                               | Zeigt Infos zum Grundstück                     |
| `/plot name <name>`                 | `/plot name <name>`                                        | Set plot name                                | Setzt den Namen des Grundstücks                |
| `/plot description <desc>`          | `/plot description <text>`                                 | Set plot description                         | Setzt die Beschreibung des Grundstücks         |
| `/plot trust <player>`              | `/plot trust <spieler>`                                    | Trust a player for this plot                 | Spieler auf Grundstück vertrauen               |
| `/plot untrust <player>`            | `/plot untrust <spieler>`                                  | Remove trusted player                        | Entfernt Vertrauensperson                      |
| `/plot trustlist`                   | `/plot trustlist`                                          | List all trusted players                     | Auflisten aller vertrauten Spieler             |
| `/plot sell <price>`                | `/plot sell <preis>`                                       | Sell plot                                    | Grundstück zum Verkauf anbieten                |
| `/plot unsell`                      | `/plot unsell`                                             | Remove plot from sale                        | Grundstück nicht mehr zum Verkauf anbieten     |
| `/plot transfer <player>`           | `/plot transfer <spieler>`                                 | Transfer plot to another player              | Grundstück an Spieler übertragen               |
| `/plot abandon`                     | `/plot abandon`                                            | Abandon the plot                             | Grundstück aufgeben                            |
| `/plot rent <pricePerDay>`          | `/plot rent <preisProTag>`                                 | Set the plot for rent (per day)              | Grundstück zur Miete anbieten (pro Tag)        |
| `/plot rentcancel`                  | `/plot rentcancel`                                         | Cancel rental of plot                        | Vermietung des Grundstücks abbrechen           |
| `/plot rentplot <days>`             | `/plot rentplot <tage>`                                    | Rent the plot for X days                     | Grundstück für X Tage mieten                   |
| `/plot rentextend <days>`           | `/plot rentextend <tage>`                                  | Extend rental by X days                      | Miete um X Tage verlängern                     |
| `/plot rate <rating 1-5>`           | `/plot rate <bewertung 1-5>`                               | Rate a plot (1-5)                            | Plot bewerten (1-5)                            |
| `/plot topplots`                    | `/plot topplots`                                           | Show top-rated plots                         | Am besten bewertete Plots anzeigen             |
| `/plot remove`                      | `/plot remove`                                             | Remove a plot (admin)                        | Grundstück entfernen (Admin)                   |

### Daily Reward Commands / Tägliche Belohnungen

| Command (English)    | Kommando (Deutsch)   | Description (ENGLISH)             | Beschreibung (DEUTSCH)             |
|----------------------|----------------------|-----------------------------------|------------------------------------|
| `/daily`             | `/daily`             | Claim daily reward                | Tägliche Belohnung abholen         |
| `/daily streak`      | `/daily streak`      | Show daily streak                 | Zeigt Serienbelohnung an           |

### Shop Commands / Shop-Befehle

| Command (English)            | Kommando (Deutsch)          | Description (ENGLISH)         | Beschreibung (DEUTSCH)           |
|------------------------------|-----------------------------|-------------------------------|----------------------------------|
| `/shop buy <item> <amount>`  | `/shop buy <item> <menge>`  | Buy item                      | Item kaufen                      |
| `/shop sell <item> <amount>` | `/shop sell <item> <menge>` | Sell item                     | Item verkaufen                   |
| `/shop prices`               | `/shop prices`              | Show all prices               | Alle Preise anzeigen             |
| `/shop info <item>`          | `/shop info <item>`         | Show info about an item       | Infos zu einem Item anzeigen     |

---

## Installation

- Download the latest release `.jar` and place it in your server's `mods` folder.
- Requires Forge 47.5.0 (Minecraft 1.20.1).
- Start the server. Configuration files are generated automatically.

---

## Configuration

Config files are located in `config/plotmod`. Adjust plot size, pricing, shop items, and permissions as needed.

---

## Permissions

Many commands require elevated permissions (e.g., admin or operator). See the `config` and server documentation for more details.

---

## Contributions

Pull requests and suggestions welcome! Please use GitHub Issues for bug reports and feature requests.

---

## License

Licensed under the MIT License. See [LICENSE](LICENSE).

---

Minecraft and Mojang Studios are trademarks of Mojang AB. This project is not affiliated with or endorsed by Mojang AB.
