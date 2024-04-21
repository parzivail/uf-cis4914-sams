package com.nitcoders.model;

public record SubjectSummaryStatistics(String subjectId, int totalWords, int totalSentences,
                                       float wordsCorrectProportion, float sentencesCorrectProportion)
{
}
