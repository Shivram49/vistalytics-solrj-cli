package TopicModel;

public class LDAPreTrainModel {
    public static void main(String[] args) {
        LDATrainer ldaTrainer = new LDATrainer("src/main/resources/emails_cleaned.csv",50,200);
        ldaTrainer.loadDataToModel(2000);
    }
}
