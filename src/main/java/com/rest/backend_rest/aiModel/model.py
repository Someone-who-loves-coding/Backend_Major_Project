from transformers import BertTokenizerFast, BertForSequenceClassification, Trainer, TrainingArguments
from datasets import load_dataset
from sklearn.metrics import precision_recall_fscore_support, accuracy_score
import numpy as np

# 1. Load pre-trained BERT model
model = BertForSequenceClassification.from_pretrained(
    "bert-base-uncased",
    num_labels=28,  # 27 emotions + neutral
    problem_type="multi_label_classification"
)

# 2. Load and preprocess GoEmotions dataset
dataset = load_dataset("go_emotions", "simplified")
tokenizer = BertTokenizerFast.from_pretrained("bert-base-uncased")

def tokenize(example):
    return tokenizer(example['text'], padding="max_length", truncation=True)

dataset = dataset.map(tokenize, batched=True)
dataset.set_format(type='torch', columns=['input_ids', 'attention_mask', 'labels'])

# 3. Define training arguments
training_args = TrainingArguments(
    output_dir="./results",
    per_device_train_batch_size=16,
    per_device_eval_batch_size=16,
    num_train_epochs=3,
    evaluation_strategy="epoch",
    logging_dir="./logs",
    logging_steps=10,
    save_total_limit=1,
    load_best_model_at_end=True,
    metric_for_best_model="f1"
)

# 4. Custom metric computation
def compute_metrics(pred):
    logits, labels = pred
    preds = (logits > 0).astype(int)
    precision, recall, f1, _ = precision_recall_fscore_support(labels, preds, average='micro')
    acc = accuracy_score(labels, preds)
    return {"accuracy": acc, "f1": f1, "precision": precision, "recall": recall}

# 5. Initialize Trainer
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=dataset["train"],
    eval_dataset=dataset["validation"],
    tokenizer=tokenizer,
    compute_metrics=compute_metrics
)


trainer.train()