public class PageResult {
    private String pageOutput;
    private String nextPage;

    public PageResult(String pageOutput, String nextPage) {
        this.pageOutput = pageOutput;
        this.nextPage = nextPage;
    }

    public String getPageOutput() {
        return pageOutput;
    }

    public String getNextPage() {
        return nextPage;
    }
}
