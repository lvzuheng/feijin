package com.data.feijin.utils;

import org.springframework.util.StringUtils;
import sun.swing.BakedArrayList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {

    public static void check() {
        List list = new ArrayList();
        list.add("1");
        System.err.println(list.hashCode());
        list.add("2");
        System.err.println(list.hashCode());
        List list2 = new ArrayList();
        list2.add("2");
        System.err.println(list2.hashCode());
        list2.add("1");
        System.err.println(list2.hashCode());
        System.err.println(list.hashCode() + "," + list2.hashCode());
    }

    public static void match() {
        String data = "var Season='2016-2019';\n" +
                "var tips='排名^球队ID^颜色值^球队名繁^英^简^赛^胜^平^负^得分^失分^净胜分^积分^';\n" +
                "var Score='|A组积分^Group A^|1^886^^泰國^Thailand^泰国^0^0^0^0^0^0^0^0^|2^887^^阿聯酋^United Arab Emirates^阿联酋^0^0^0^0^0^0^0^0^|3^893^^印度^India^印度^0^0^0^0^0^0^0^0^|4^900^^巴林^Bahrain^巴林^0^0^0^0^0^0^0^0^|B组积分^Group B^|1^879^^巴勒斯坦^Palestine^巴勒斯坦^0^0^0^0^0^0^0^0^|2^881^^約旦^Jordan^约旦^0^0^0^0^0^0^0^0^|3^899^^敘利亞^Syrian^叙利亚^0^0^0^0^0^0^0^0^|4^913^^澳洲^Australia^澳大利亚^0^0^0^0^0^0^0^0^|C组积分^Group C^|1^885^^吉爾吉斯坦^Kyrgyzstan^吉尔吉斯斯坦^0^0^0^0^0^0^0^0^|2^896^^中國^China^中国^0^0^0^0^0^0^0^0^|3^898^^南韓^Korea Republic^韩国^0^0^0^0^0^0^0^0^|4^1562^^菲律賓^Philippines^菲律宾^0^0^0^0^0^0^0^0^|D组积分^Group D^|1^783^^伊朗^Iran^伊朗^0^0^0^0^0^0^0^0^|2^874^^伊拉克^Iraq^伊拉克^0^0^0^0^0^0^0^0^|3^877^^也門^Yemen^也门^0^0^0^0^0^0^0^0^|4^883^^越南^Vietnam^越南^0^0^0^0^0^0^0^0^|E组积分^Group E^|1^897^^黎巴嫩^Lebanon^黎巴嫩^0^0^0^0^0^0^0^0^|2^891^^沙地阿拉伯^Saudi Arabia^沙特阿拉伯^0^0^0^0^0^0^0^0^|3^876^^北韓^Korea DPR^朝鲜^0^0^0^0^0^0^0^0^|4^904^^卡塔爾^Qatar^卡塔尔^0^0^0^0^0^0^0^0^|F组积分^Group F^|1^825^^土庫曼^Turkmenistan^土库曼斯坦^0^0^0^0^0^0^0^0^|2^875^^烏茲別克^Uzbekistan^乌兹别克^0^0^0^0^0^0^0^0^|3^902^^阿曼^Oman^阿曼^0^0^0^0^0^0^0^0^|4^903^^日本^Japan^日本^0^0^0^0^0^0^0^0^|备注：有颜色球队表示已出线球队^Remark: The colored team has qualified for the next round.^';\n" +
                "var GroupName='分组赛';\n" +
                "\n" +
                "var Score='|A组积分^Group A^|1^885^#ff0000^吉爾吉斯坦^Kyrgyzstan^吉尔吉斯斯坦^6^4^1^1^14^8^6^13^|2^893^#ff0000^印度^India^印度^6^4^1^1^11^5^6^13^|3^1563^^緬甸^Myanmar^缅甸联邦^6^2^2^2^10^10^0^8^|4^3325^^中國澳門^Macau of China^中国澳门^6^0^0^6^4^16^-12^0^|B组积分^Group B^|1^897^#ff0000^黎巴嫩^Lebanon^黎巴嫩^6^5^1^0^14^4^10^16^|2^876^#ff0000^北韓^Korea DPR^朝鲜^6^3^2^1^13^10^3^11^|3^888^^中國香港^Hong Kong^中国香港^6^1^2^3^4^7^-3^5^|4^889^^馬來西亞^Malaysia^马来西亚^6^0^1^5^5^15^-10^1^|C组积分^Group C^|1^881^#ff0000^約旦^Jordan^约旦^6^3^3^0^16^5^11^12^|2^883^#ff0000^越南^Vietnam^越南^6^2^4^0^9^3^6^10^|3^6651^^阿富汗^Afghanistan^阿富汗^6^1^3^2^7^10^-3^6^|4^1565^^柬埔寨^Cambodia^柬埔寨^6^1^0^5^3^17^-14^3^|D组积分^Group D^|1^902^#ff0000^阿曼^Oman^阿曼^6^5^0^1^28^5^23^15^|2^879^#ff0000^巴勒斯坦^Palestine^巴勒斯坦^6^5^0^1^25^3^22^15^|3^882^^馬爾代夫^Maldives^马尔代夫^6^2^0^4^11^19^-8^6^|4^7818^^不丹^Bhutan^不丹^6^0^0^6^2^39^-37^0^|E组积分^Group E^|1^900^#ff0000^巴林^Bahrain^巴林^6^4^1^1^15^3^12^13^|2^825^#ff0000^土庫曼^Turkmenistan^土库曼斯坦^6^3^1^2^9^10^-1^10^|3^5288^^中國臺北^Chinese Taipei^中国台北^6^3^0^3^7^12^-5^9^|4^892^^新加坡^Singapore^新加坡^6^0^2^4^3^9^-6^2^|F组积分^Group F^|1^1562^#ff0000^菲律賓^Philippines^菲律宾^6^3^3^0^13^8^5^12^|2^877^#ff0000^也門^Yemen^也门^6^2^4^0^7^5^2^10^|3^884^^塔吉克斯坦^Tajikistan^塔吉克斯坦^6^2^1^3^10^9^1^7^|4^6650^^尼泊爾^Nepal^尼泊尔^6^0^2^4^3^11^-8^2^|备注：有颜色球队表示已出线球队^Remark: The colored team has qualified for the next round.^';\n" +
                "var GroupName='资格赛';\n" +
                "\n" +
                "var Score='';\n" +
                "var GroupName='资格赛预1';";
        String mKey = "((?<=(var\\s?)).*(?=\\s?=))|((?<=(=)).*(?=\\s?;))";
        Matcher matcher = Pattern.compile(mKey).matcher(data);
        Map<String, String> map = new HashMap();
        int i = 0;
        List<String> list = new ArrayList();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        while (i < list.size()) {
            map.put(list.get(i).trim(), list.get(++i).trim());
            i++;
        }
    }


    private static void parseGroupData(List<String> list,String leagueId) {

    }
}
