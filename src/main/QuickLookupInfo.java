package main;

import java.util.ArrayList;

@SuppressWarnings("SpellCheckingInspection")
public class QuickLookupInfo {

    private static ArrayList<String[]> channelInfo;

    public static ArrayList<String[]> getQuickLookupInfo() {
        channelInfo = new ArrayList<>();

        storeChannel("Samara Redway", "UCLdXZU1bFk-BzztF7i6WvSw");
        storeChannel("RealLifeLore", "UCP5tjEmvPItGyLhmjdwP7Ww");
        storeChannel("Wendover Productions", "UC9RM-iSvTu1uPJb8X5yp3EQ");
        storeChannel("Half as Interesting", "UCuCkxoKLYO_EQ2GeFtbM_bw");
        storeChannel("Kurzgesagt – In a Nutshell", "UCsXVk37bltHxD1rDPwtNM8Q");
        storeChannel("Practical Engineering", "UCMOqf8ab-42UUQIdVoKwjlQ");
        storeChannel("Swiss001", "UCYiaHzwtsww6phfxwUtZv8w");
        storeChannel("MrBeast", "UCX6OQ3DkcsbYNE6H8uQQuVA");
        storeChannel("MrBeast Gaming", "UCIPPMRA040LQr5QPyJEbmXA");
        storeChannel("Wadzee", "UCRlEFn0L2G_DktbyvN0AZ5A");
        storeChannel("Mumbo Jumbo", "UChFur_NwVSbUozOcF_F2kMg");
        storeChannel("Grian", "UCR9Gcq0CMm6YgTzsDxAxjOQ");
        storeChannel("Fundy", "UCCE5NVlm0FhbunMMBT48WAw");
        storeChannel("xisumavoid", "UCU9pX8hKcrx06XfOB-VQLdw");
        storeChannel("JerryRigEverything", "UCWFKCr40YwOZQx8FHU_ZqqQ");
        storeChannel("Marques Brownlee", "UCBJycsmduvYEL83R_U4JriQ");
        storeChannel("How Ridiculous", "UC5f5IV0Bf79YLp_p9nfInRA");
        storeChannel("Stuff Made Here", "UCj1VqrHhDte54oLgPG4xpuQ");
        storeChannel("Mark Rober", "UCY1kMZp36IQSyNx_9h4mpCg");
        storeChannel("Nile Red Shorts", "UCA0mlN90EHCizvo101nbr-g");
        storeChannel("Tom Scott", "UCBa659QWEk1AI4Tg--mrJ2A");
        storeChannel("DoodleChaos", "UCnVimh35ecf46P_Bt-2AArg");
        storeChannel("Foo the Flowerhorn", "UC3Kk8tvhHkWMMfKBb0q0X9g");

        return channelInfo;
    }

    public static void storeChannel(String name, String id) {
        channelInfo.add(new String[] {name, id});
    }
}