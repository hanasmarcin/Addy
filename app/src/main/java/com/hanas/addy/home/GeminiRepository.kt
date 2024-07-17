package com.hanas.addy.home

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.hanas.addy.BuildConfig


class GeminiRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    suspend fun generateContent(content: List<Bitmap>, prompt: String): GenerateContentResponse {
        return generativeModel.generateContent(
            content {
                content.forEach { bitmap ->
                    image(bitmap)
                }
                text(PROMPT)
            }
        )

    }
}

const val PROMPT = """
You are a teacher known for your meticulous attention to detail, preparing a comprehensive card game for your students to learn about a variety of subjects. Your goal is to design a set of **at least 30** multiple-choice question cards using ONLY the information found in the provided visual materials (textbook pages, slides, etc.).  These cards should thoroughly test the following key areas of knowledge, focusing on the concepts and relationships presented:

* **Key Facts and Figures:**  Questions about significant numbers, statistics, or key findings related to the topic.
* **Cause and Effect Relationships:** Questions exploring how different elements or events influence each other within the subject matter.
* **Definitions and Terminology:**  Questions testing understanding of important terms and concepts.
* **Comparisons and Contrasts:**  Questions that require students to identify similarities, differences, or patterns between different elements.

Each card must have:

* **A multiple-choice question:** With 4 distinct answer choices (A, B, C, D), and only one correct answer.  The question should focus on **concepts, key figures, comparisons, or significant details** related to the four key areas listed above.
* **Card Title:** A concise and engaging title reflecting the question's theme, **related to the attributes.** It should sound like a character name, as we will be generating images and descriptions for the cards.
* **Short Description:**  A brief and vivid explanation of the card's topic (1-2 sentences), **related to the attributes.**  This description should also have a character-like quality.
* **Three Attributes:** red, green and blue. Each attribute will be an object with a "name" and a "value" field, with value within 1-10.

**Crucially, the JSON output MUST be in perfect format. It will be used to power a mobile app, so any errors will prevent the game from functioning. Please double-check your JSON to ensure it is completely valid and parsable.**

Provide your cards in JSON format, following this structure:

```json
[
  {
    "question": "Question text here (focus on the provided materials)",
    "A": "Answer option A",
    "B": "Answer option B",
    "C": "Answer option C",
    "D": "Answer option D",
    "answer": "Correct answer letter (A, B, C, or D)",
    "title": "Character-like Card Title (relates to question and attributes)",
    "description": "Character-like description of the card's topic (relates to question and attributes).",
    "attributes": {
      "green": {
        "name": "Thematic Attribute Name 1", 
        "value": 5 // Example value
      },
      "blue": {
        "name": "Thematic Attribute Name 2", 
        "value": 8 // Example value
      },
      "red": {
        "name": "Thematic Attribute Name 3", 
        "value": 3  // Example value
      }
    }
  },
  // ... more cards in the same format ...
]
```

Ensure each question:

* Covers a unique piece of information from the visuals – avoid redundancy.
* Varies in difficulty, reflected in the attribute values (higher values = more difficult). 
* Has a cohesive theme throughout the card (title, description, attributes, and question). 
* Is appropriate for a student learning about the subject matter, avoiding irrelevant or overly specialized details. 
"""