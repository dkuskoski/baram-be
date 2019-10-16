package mk.com.barambe;

import java.util.Arrays;
import java.util.List;

public class BaramConstants {

    public final static String HOME = "Дома";
    public final static String MAKEDONIJA = "Македонија";
    public final static String EKONOMIJA = "Економија";
    public final static String SVET = "Свет";
    public final static String HRONIKA = "Хроника";
    public final static String SPORT = "Спорт";
    public final static String ZABAVA = "Забава";
    public final static String KULTURA = "Култура";
    public final static String ADULT = "18+";
    public static final String API_USERNAME = "";
    public static final String API_PASSWORD = "";
    public static final String MOST_VIEWED = "Најгледано";
    public static final String POST = "post";

    public static List<String> getCategories(){
        return Arrays.asList(HOME, MAKEDONIJA, EKONOMIJA, SVET,
                HRONIKA, SPORT, ZABAVA, KULTURA, MOST_VIEWED, ADULT);
    }
}
