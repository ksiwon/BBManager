import spacy
from soynlp.tokenizer import LTokenizer
import urllib.request
from soynlp import DoublespaceLineCorpus
from soynlp.word import WordExtractor

corpus = DoublespaceLineCorpus("2016-10-20.txt")

word_extractor = WordExtractor()
word_extractor.train(corpus)
word_score_table = word_extractor.extract()
#질문 전처리
scores = {word:score.cohesion_forward for word, score in word_score_table.items()}
l_tokenizer = LTokenizer(scores=scores)
l_tokenizer.tokenize("지금 타자의 타율은?", flatten=False)

