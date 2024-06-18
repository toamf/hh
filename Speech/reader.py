import joblib


vectorizer_file_path = 'tfidf_vectorizer.pkl'
model_file_path = 'logistic_regression_model.pkl'

tfidf_vectorizer = joblib.load(vectorizer_file_path)
logistic_regression_model = joblib.load(model_file_path)

print(tfidf_vectorizer, logistic_regression_model)
