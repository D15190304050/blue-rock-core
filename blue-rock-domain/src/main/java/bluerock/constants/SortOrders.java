package bluerock.constants;

public class SortOrders
{
    private SortOrders()
    {}

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    public static boolean matchAsc(String s)
    {
        return ASC.equalsIgnoreCase(s);
    }

    public static boolean matchDesc(String s)
    {
        return DESC.equalsIgnoreCase(s);
    }
}
