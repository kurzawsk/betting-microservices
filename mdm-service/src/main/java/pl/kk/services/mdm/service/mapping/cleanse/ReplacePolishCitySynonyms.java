package pl.kk.services.mdm.service.mapping.cleanse;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Component
public class ReplacePolishCitySynonyms implements CleanseFunction {

    private static final Map<String, String> DICTIONARY = new TreeMap<>((s1, s2) -> {
        int result = Long.compare(s2.length(), (long) s1.length());
        if (result == 0) {
            return s1.compareTo(s2);
        } else {
            return result;
        }
    }
    );

    static {
        DICTIONARY.putAll(ImmutableMap.<String, String>builder().
                put("AFGANISTAN", "AFGHANISTAN").
                put("ALBANIA", "ALBANIA").put("ALGIERIA", "ALGERIA").put("ANDORA", "ANDORRA").
                put("ANGOLA", "ANGOLA").put("ANTIGUA I BARBUDA", "ANTIGUA AND BARBUDA").
                put("ARABIA SAUDYJSKA", "SAUDI ARABIA").put("ARGENTYNA", "ARGENTINA").
                put("ARMENIA", "ARMENIA").put("AUSTRALIA", "AUSTRALIA").put("AUSTRIA", "AUSTRIA").
                put("AZERBEJDĽAN", "AZERBAIJAN").put("BAHAMY", "BAHAMAS").put("BAHRAJN", "BAHRAIN").
                put("BANGLADESZ", "BANGLADESH").put("BARBADOS", "BARBADOS").put("BELGIA", "BELGIUM").
                put("BELIZE", "BELIZE").put("BENIN", "BENIN").put("BHUTAN", "BHUTAN").put("BIALORUS", "BELARUS").
                put("BIRMA", "MYANMAR").put("BOLIWIA", "BOLIVIA").put("BOSNIA I HERCEGOWINA", "BOSNIA AND HERZEGOVINA").
                put("BOTSWANA", "BOTSWANA").put("BRAZYLIA", "BRAZIL").put("BRUNEI", "BRUNEI").put("BULGARIA", "BULGARIA").
                put("BURKINA FASO", "BURKINA FASO").put("BURUNDI", "BURUNDI").put("CHILE", "CHILE").put("CHINY", "CHINA").
                put("CHORWACJA", "CROATIA").put("CYPR", "CYPRUS").put("CZAD", "CHAD").put("CZARNOGORA", "MONTENEGRO").
                put("CZECHY", "CZECH REPUBLIC").put("DANIA", "DENMARK").put("DEMOKRATYCZNA REPUBLIKA", "DEMOCRATIC REPUBLIC OF THE").
                put("KONGA", "CONGO").put("DOMINIKA", "DOMINICA").put("DOMINIKANA", "DOMINICAN REPUBLIC").put("DĽIBUTI", "DJIBOUTI").
                put("EGIPT", "EGYPT").put("EKWADOR", "ECUADOR").put("ERYTREA", "ERITREA").put("ESTONIA", "ESTONIA").put("ETIOPIA", "ETHIOPIA").
                put("FIDĽI", "FIJI").put("FILIPINY", "PHILIPPINES").put("FINLANDIA", "FINLAND").put("FRANCJA", "FRANCE").put("GABON", "GABON").
                put("GAMBIA", "GAMBIA").put("GHANA", "GHANA").put("GRECJA", "GREECE").put("GRENADA", "GRENADA").put("GRUZJA", "GEORGIA").
                put("GUJANA", "GUYANA").put("GWATEMALA", "GUATEMALA").put("GWINEA", "GUINEA").put("GWINEA BISSAU", "GUINEA-BISSAU").
                put("GWINEA ROWNIKOWA", "EQUATORIAL GUINEA").put("HAITI", "HAITI").put("HISZPANIA", "SPAIN").put("HOLANDIA", "NETHERLANDS").
                put("HONDURAS", "HONDURAS").put("INDIE", "INDIA").put("INDONEZJA", "INDONESIA").put("IRAK", "IRAQ").put("IRAN", "IRAN").
                put("IRLANDIA", "IRELAND").put("ISLANDIA", "ICELAND").put("IZRAEL", "ISRAEL").put("JAMAJKA", "JAMAICA").put("JAPONIA", "JAPAN").
                put("JEMEN", "YEMEN").put("JORDANIA", "JORDAN").put("KAMBODĽA", "CAMBODIA").put("KAMERUN", "CAMEROON").put("KANADA", "CANADA").
                put("KATAR", "QATAR").put("KAZACHSTAN", "KAZAKHSTAN").put("KENIA", "KENYA").put("KIRGISTAN", "KYRGYZSTAN").put("KIRIBATI", "KIRIBATI").
                put("KOLUMBIA", "COLOMBIA").put("KOMORY", "COMOROS").put("KONGO", "CONGO").put("KOREA POLUDNIOWA", "REPUBLIC OF KOREA SOUTH KOREA").
                put("KOSTARYKA", "COSTA RICA").put("KUBA", "CUBA").put("KUWEJT", "KUWAIT").put("LESOTHO", "LESOTHO").put("LIBAN", "LEBANON").
                put("LIBERIA", "LIBERIA").put("LIECHTENSTEIN", "LIECHTENSTEIN").put("LITWA", "LITHUANIA").put("LUKSEMBURG", "LUXEMBOURG").
                put("LOTWA", "LATVIA").put("MADAGASKAR", "MADAGASCAR").put("MALAWI", "MALAWI").put("MALEDIWY", "MALDIVES").put("MALEZJA", "MALAYSIA").
                put("MALI", "MALI").put("MALTA", "MALTA").put("MIRKONEZJA", "MICRONESIA").put("MAROKO", "MOROCCO").put("MAURETANIA", "MAURITANIA").
                put("MAURITIUS", "MAURITIUS").put("MEKSYK", "MEXICO").put("MIKRONEZJA", "MICRONESIA").put("MOLDAWIA", "MOLDOVA").put("MONAKO", "MONACO").
                put("MONGOLIA", "MONGOLIA").put("MOZAMBIK", "MOZAMBIQUE").put("NAMIBIA", "NAMIBIA").put("NAURU", "NAURU").put("NEPAL", "NEPAL").
                put("NIEMCY", "GERMANY").put("NIGER", "NIGER").put("NIGERIA", "NIGERIA").put("NIKARAGUA", "NICARAGUA").put("NORWEGIA", "NORWAY").
                put("NOWA ZELANDIA", "NEW ZEALAND").put("OMAN", "OMAN").put("PAKISTAN", "PAKISTAN").put("PALAU", "PALAU").put("PANAMA", "PANAMA").
                put("PAPUA NOWA GWINEA", "PAPUA NEW GUINEA").put("PARAGWAJ", "PARAGUAY").put("PERU", "PERU").put("POLSKA", "POLAND").
                put("PORTUGALIA", "PORTUGAL").put("REPUBLIKA POLUDNIOWEJ AFRYKI", "SOUTH AFRICA").put("REPUBLIKA SRODKOWOAFRYKNSKA", "CENTRAL AFRICAN REPUBLIC").
                put("REPUBLIKA ZIELONEGO PRZYLĄDKA", "CAPE VERDE").put("ROSJA", "RUSSIA").put("RUMUNIA", "ROMANIA").put("RWANDA", "RWANDA").
                put("SAINT KITTS I NEVIS", "SAINT KITTS AND NEVIS").put("SAINT LUCIA", "SAINT LUCIA").put("SALWADOR", "EL SALVADOR").
                put("SAMOA", "SAMOA").put("SAN MARINO", "SAN MARINO").put("SENEGAL", "SENEGAL").put("SERBIA", "SERBIA").put("SESZELE", "SEYCHELLES").
                put("SIERRA LEONE", "SIERRA LEONE").put("SINGAPUR", "SINGAPORE").put("SLOWACJA", "SLOVAKIA").put("SLOWENIA", "SLOVENIA").put("SOMALIA", "SOMALIA").
                put("SRI LANKA", "SRI LANKA").put("STANY ZJEDNOCZONE", "UNITED STATES OF AMERICA").put("SUAZI", "SWAZILAND").put("SUDAN", "SUDAN").
                put("SURINAM", "SURINAME").put("SYRIA", "SYRIA").put("SZWAJCARIA", "SWITZERLAND").put("SZWECJA", "SWEDEN").put("TADZYKISTAN", "TAJIKISTAN").
                put("TAJLANDIA", "THAILAND").put("TANZANIA", "TANZANIA").put("TIMOR WSCHODNI", "EAST TIMOR").put("TOGO", "TOGO").put("TONGA", "TONGA").
                put("TRYNIDAD I TOBAGO", "TRINIDAD AND TOBAGO").put("TUNEZJA", "TUNISIA").put("TURCJA", "TURKEY").put("TURKMENISTAN", "TURKMENISTAN").
                put("TUVALU", "TUVALU").put("UGANDA", "UGANDA").put("UKRAINA", "UKRAINE").put("URUGWAJ", "URUGUAY").put("UZBEKISTAN", "UZBEKISTAN").
                put("VANUATU", "VANUATU").put("WATYKAN", "VATICAN CITY").put("WENEZUELA", "VENEZUELA").put("WEGRY", "HUNGARY").put("WIELKA BRYTANIA", "UNITED KINGDOM").
                put("WIETNAM", "VIETNAM").put("WLOCHY", "ITALY").put("WYBRZEZE KOSCI SLONIOWEJ", "IVORY COAST").put("WYSPY MARSHALLA", "MARSHALL ISLANDS").
                put("WYSPY SALOMONA", "SOLOMON ISLANDS").put("WYSPY SWIETEGO TOMASZA I KSIAZCA", "SAO TOME AND PRINCIPE").put("ZAMBIA", "ZAMBIA").
                put("ZIMBABWE", "ZIMBABWE").put("ZJEDNOCZONE EMIRATY ARABSKIE", "UNITED ARAB EMIRATES").build());
    }


    @Override
    public Optional<String> cleanse(String input) {
        for (Map.Entry<String, String> e : DICTIONARY.entrySet()) {
            input = input.replace(e.getKey(), e.getValue());
        }
        return Optional.of(removeAdditionalSpaces(input));
    }
}
