package Querying.KWIC;

public class KeywordInContext {
    private String keyword;
    private int contextLength;
    private String sentence;

    public KeywordInContext(String keyword, int contextLength, String sentence) {
        this.keyword = keyword;
        this.contextLength = contextLength;
        this.sentence = sentence;

    }
    public String highlightKeywordInContext() {
        sentence.replaceAll("[^a-zA-Z0-9?!.: ]","");
        sentence.replaceAll("Subject:","");
        sentence.replaceAll("\\n","");
        StringBuilder highlightedText = new StringBuilder();

        int keywordIndex = sentence.indexOf(keyword);
        if (keywordIndex != -1) {
            int start = Math.max(0, keywordIndex - contextLength);
            int end = Math.min(sentence.length(), keywordIndex + keyword.length() + contextLength);

            highlightedText.append("...");

            if (start > 0) {
                String beforeKeyword = sentence.substring(start, keywordIndex);
                beforeKeyword = beforeKeyword.replaceAll("[^a-zA-Z0-9?!.: ]", "");
                highlightedText.append(beforeKeyword);
            }

            highlightedText.append("*").append(sentence.substring(keywordIndex, keywordIndex + keyword.length())).append("*");

            if (end < sentence.length()) {
                String afterKeyword = sentence.substring(keywordIndex + keyword.length(), end);
                afterKeyword = afterKeyword.replaceAll("[^a-zA-Z0-9?!.: ]", "");
                highlightedText.append(afterKeyword);
            }

            highlightedText.append("...");
        } else {
            highlightedText.append("");
        }

        return highlightedText.toString();
    }
}
