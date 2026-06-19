package com.example.data

import kotlinx.coroutines.flow.Flow

data class Weapon(
    val id: String,
    val name: String,
    val category: String,
    val damage: Float,       // 0 to 100
    val fireRate: Float,     // 0 to 100
    val stability: Float,    // 0 to 100 (higher means less recoil/more stable)
    val range: Float,        // 0 to 100
    val optimalMag: String,
    val description: String,
    val ammoType: String,
    val bulletVelocity: String
)

data class DropZone(
    val id: String,
    val name: String,
    val mapName: String,
    val x: Float,            // Normalized 0f to 1f for custom canvas rendering overlay
    val y: Float,            // Normalized 0f to 1f for custom canvas rendering overlay
    val lootGrade: String,   // High, Extreme, Medium
    val riskLevel: String,   // S-Tier, A-Tier, B-Tier
    val vehicleSpawnRate: Int, // Percentage (e.g. 80)
    val details: String,
    val tacticalTip: String
)

class CompanionRepository(private val buildDao: TacticalBuildDao) {

    // Repository access to Custom Saved Builds (Room)
    val savedBuilds: Flow<List<TacticalBuild>> = buildDao.getAllBuilds()

    suspend fun saveBuild(build: TacticalBuild) {
        buildDao.insertBuild(build)
    }

    suspend fun deleteBuild(build: TacticalBuild) {
        buildDao.deleteBuild(build)
    }

    suspend fun deleteBuildById(id: Int) {
        buildDao.deleteBuildById(id)
    }

    // Static curated weapons data (Highly accurate to actual PUBG mechanics)
    val weapons = listOf(
        Weapon(
            id = "m416",
            name = "M416",
            category = "Assault Rifle",
            damage = 41f,
            fireRate = 78f,
            stability = 82f,
            range = 65f,
            optimalMag = "40 Rounds (Extended)",
            description = "The most reliable all-rounder. Highly customizable with 5 attachment slots. Negligible recoil when fully loaded with a vertical grip and tactical stock.",
            ammoType = "5.56mm",
            bulletVelocity = "880 m/s"
        ),
        Weapon(
            id = "akm",
            name = "AKM",
            category = "Assault Rifle",
            damage = 47f,
            fireRate = 60f,
            stability = 55f,
            range = 60f,
            optimalMag = "40 Rounds (Extended)",
            description = "High stopping power but heavy muzzle kick. Devastating in close-quarters combat. Experienced players use a compensator to tame its vertical recoil.",
            ammoType = "7.62mm",
            bulletVelocity = "715 m/s"
        ),
        Weapon(
            id = "scarl",
            name = "SCAR-L",
            category = "Assault Rifle",
            damage = 41f,
            fireRate = 72f,
            stability = 80f,
            range = 62f,
            optimalMag = "40 Rounds (Extended)",
            description = "Very stable and easy to control. Slightly slower fire rate than M416 but provides outstanding horizontal recoil pattern predictability during full-auto sprays.",
            ammoType = "5.56mm",
            bulletVelocity = "870 m/s"
        ),
        Weapon(
            id = "awm",
            name = "AWM",
            category = "Sniper Rifle",
            damage = 105f,
            fireRate = 12f,
            stability = 40f,
            range = 100f,
            optimalMag = "7 Rounds (Sniper Ext.)",
            description = "Air-drop exclusive bolt-action rifle. The pinnacle of marksman power. Guarantees a direct one-shot knock against level 3 helmets over huge distances.",
            ammoType = ".300 Magnum",
            bulletVelocity = "945 m/s"
        ),
        Weapon(
            id = "m24",
            name = "M24",
            category = "Sniper Rifle",
            damage = 75f,
            fireRate = 15f,
            stability = 45f,
            range = 92f,
            optimalMag = "7 Rounds (Sniper Ext.)",
            description = "High-velocity bolt action sniper accessible in normal spawns. Boasts cleaner trajectory than the Kar98k and can accept extended sniper magazines.",
            ammoType = "7.62mm",
            bulletVelocity = "790 m/s"
        ),
        Weapon(
            id = "kar98k",
            name = "Kar98k",
            category = "Sniper Rifle",
            damage = 79f,
            fireRate = 14f,
            stability = 38f,
            range = 88f,
            optimalMag = "5 Rounds (Internal)",
            description = "Vintage bolt-action sniper. A fan staple. Delivers extremely high headshot multipliers that instantly knock down Level 1 or 2 helmets.",
            ammoType = "7.62mm",
            bulletVelocity = "760 m/s"
        ),
        Weapon(
            id = "ump45",
            name = "UMP45",
            category = "SMG",
            damage = 38f,
            fireRate = 85f,
            stability = 95f,
            range = 35f,
            optimalMag = "35 Rounds (Extended)",
            description = "The ultimate close-range spray machine. Laser-like hip-fire precision with almost zero recoil. Highly recommended for close quarters urban combat.",
            ammoType = ".45 ACP",
            bulletVelocity = "400 m/s"
        ),
        Weapon(
            id = "groza",
            name = "Groza",
            category = "Assault Rifle",
            damage = 47f,
            fireRate = 90f,
            stability = 65f,
            range = 55f,
            optimalMag = "40 Rounds (Extended)",
            description = "Airdrop-exclusive bullpup rifle. Fuses the immense damage of AKM's 7.62mm ammunition with the blazing fire rate of an SMG. Melt down squads instantly.",
            ammoType = "7.62mm",
            bulletVelocity = "715 m/s"
        ),
        Weapon(
            id = "dbs",
            name = "DBS",
            category = "Shotgun",
            damage = 96f,
            fireRate = 45f,
            stability = 50f,
            range = 15f,
            optimalMag = "14 Rounds (Internal)",
            description = "Double-barrel pump shotgun with immense close-quarters wipe potential. Emits a massive blast of buckshot pellets that can clear a staircase in a single tap.",
            ammoType = "12 Gauge",
            bulletVelocity = "360 m/s"
        )
    )

    // Strategic Drop Zones for Map Coordinates (Normalized 0f to 100f layout coordinates)
    val dropZones = listOf(
        // Erangel hotspots
        DropZone(
            id = "pochinki",
            name = "Pochinki",
            mapName = "Erangel",
            x = 48f,
            y = 52f,
            lootGrade = "High Tier",
            riskLevel = "Extreme (Hot-Drop)",
            vehicleSpawnRate = 90,
            details = "Situated right in the center of Erangel. Hosts a dense network of residential brick houses containing highly saturated active tier weapon caches.",
            tacticalTip = "Beware of secondary roof camping snipers. Use wall climbs and hallways to trap opponents inside standard double-story houses."
        ),
        DropZone(
            id = "military_base",
            name = "Sosnovka Military Base",
            mapName = "Erangel",
            x = 52f,
            y = 85f,
            lootGrade = "Extreme Tech",
            riskLevel = "Extreme (High-Tier)",
            vehicleSpawnRate = 75,
            details = "An isolated southern military island. Massive metal hangar buildings and radar dishes packed with top-shelf level 3 gear and sniper attachments.",
            tacticalTip = "Secure the high radar satellite towers early to cover exits. Watch out for bridge blockers when migrating across to main Erangel block!"
        ),
        DropZone(
            id = "school",
            name = "School",
            mapName = "Erangel",
            x = 55f,
            y = 44f,
            lootGrade = "High (Dense)",
            riskLevel = "High (Instant)",
            vehicleSpawnRate = 80,
            details = "Centered facility containing an auditorium, pool, classrooms and a secondary sprawling block of apartment complexes nearby.",
            tacticalTip = "Extremely condensed combat starting circles. Grab any submachine gun or shotgun immediately. Take control of the central pool roof."
        ),
        DropZone(
            id = "georgopol",
            name = "Georgopol Container Crate Port",
            mapName = "Erangel",
            x = 18f,
            y = 35f,
            lootGrade = "Extreme Tech",
            riskLevel = "High (Spacious)",
            vehicleSpawnRate = 85,
            details = "A massive maritime docking port comprising colored shipping container stacks, large warehouses, and three prominent loading crane grids.",
            tacticalTip = "Jump on top of horizontal container rows for supreme long angles. Keep hearing alerts active for flanking sounds through metal alleys."
        ),
        DropZone(
            id = "novorepnoye",
            name = "Novorepnoye Port",
            mapName = "Erangel",
            x = 75f,
            y = 80f,
            lootGrade = "High Tier",
            riskLevel = "High (Isolated)",
            vehicleSpawnRate = 70,
            details = "An eastern docklands version of Georgopol with abundant container rows, boat spawns along shoreline docks, and steep surrounding cliff paths.",
            tacticalTip = "Use shoreline speedboats as instant safe evacuations if circle shifts away. Avoid getting pinned inside warehouses without window exits."
        ),
        
        // Miramar hotspots
        DropZone(
            id = "pecado",
            name = "Pecado Casino Arena",
            mapName = "Miramar",
            x = 47f,
            y = 54f,
            lootGrade = "Extreme Elite",
            riskLevel = "Extreme (Hot-Drop)",
            vehicleSpawnRate = 95,
            details = "A dead-center desert town anchored by a huge four-story sports stadium boxing ring, luxury casino, and high-rise apartments.",
            tacticalTip = "Landing directly in the boxing ring provides immediate automatic gear but guarantees high-risk gunfights. Grab a shotgun or vector and clear fast!"
        ),
        DropZone(
            id = "hacienda",
            name = "Hacienda del Patron",
            mapName = "Miramar",
            x = 60f,
            y = 38f,
            lootGrade = "Extreme Tech",
            riskLevel = "Extreme (Dangerous)",
            vehicleSpawnRate = 60,
            details = "A luxurious compact villa compound featuring inner courtyard gardens, dynamic open hallways, and premium gun spawns.",
            tacticalTip = "Speed is paramount. Clear central courtyard arches first. Use second-floor balconies to intercept teams pushing from surrounding desert flats."
        ),
        DropZone(
            id = "san_martin",
            name = "San Martin Town",
            mapName = "Miramar",
            x = 52f,
            y = 30f,
            lootGrade = "High Tier",
            riskLevel = "High (Dense)",
            vehicleSpawnRate = 80,
            details = "A large dusty city built into the side of a massive desert mountain ridge, filled with construction sites, brick homes, and a police station.",
            tacticalTip = "Secure the high-elevation concrete ridge villas at the northern town border to establish scout lines on the entire city."
        )
    )
}
