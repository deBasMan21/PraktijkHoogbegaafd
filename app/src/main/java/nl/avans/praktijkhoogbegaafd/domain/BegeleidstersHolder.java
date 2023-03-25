package nl.avans.praktijkhoogbegaafd.domain;

import java.util.HashMap;
import java.util.Map;

public class BegeleidstersHolder {
    public static BegeleidstersHolder standard = new BegeleidstersHolder();

    public Map<String, String> begeleidsters = new HashMap<>();
}
