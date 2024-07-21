package com.hanas.addy.repository

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

    suspend fun generateCardStack(content: List<Bitmap>): GenerateContentResponse {
        return generativeModel.generateContent(
            content {
                content.forEach { bitmap ->
                    image(bitmap)
                }
                text(PROMPT)
            }
        )
    }

    suspend fun correctCardStackJsonResponse(content: List<Bitmap>, json: String, errorMessage: String): GenerateContentResponse {
        return generativeModel.generateContent(
            content {
                content.forEach { bitmap ->
                    image(bitmap)
                }
                text(String.format(CORRECTION_PROMPT, json, errorMessage))
            }
        )
    }
}

const val PROMPT = """
YOUR PRIMARY OBJECTIVE IS TO GENERATE PERFECTLY VALID JSON. FAILURE TO DO SO WILL MAKE THE OUTPUT COMPLETELY UNUSABLE AND LEAD TO SYSTEM ERRORS.

You are a teacher known for your meticulous attention to detail, preparing a comprehensive card game for your students to learn about a variety of subjects. Your goal is to design a set of at least 70 unique multiple-choice question cards using ONLY the information found in the provided visual materials (textbook pages, slides, etc.). It is **crucially important** that the final card stack contains **no less than 70 cards**. These cards should thoroughly test the following key areas of knowledge, focusing on the concepts and relationships presented:

* **Key Facts and Figures:**  Questions about significant numbers, statistics, or key findings related to the topic.
* **Cause and Effect Relationships:** Questions exploring how different elements or events influence each other within the subject matter.
* **Definitions and Terminology:**  Questions testing understanding of important terms and concepts.
* **Comparisons and Contrasts:**  Questions that require students to identify similarities, differences, or patterns between different elements.

IMPORTANT: Players will see a card's title, description, and attributes BEFORE seeing the question and answers.  Therefore, these elements MUST NOT give away the answer. They should provide thematic context related to the card's general topic, but NOT include specific details that would reveal the correct answer.

You will create a JSON string representing the card stack, including global attribute names for the entire stack.  Examine the provided screenshots and choose three thematic attribute names related to the subject matter. 

Each card in the stack must be represented as a JSON object with the following fields:

* `"question"`: The text of the multiple-choice question.
* `"a"`, `"b"`, `"c"`, `"d"`: The four answer options for the question.
* `"answer"`: The correct answer, represented as the letter of the correct answer choice ("a", "b", "c", or "d").
* `"title"`: A concise and engaging title reflecting the question's theme. It should sound like a non-human character name, such as a personified animal, mythical creature, or object (e.g., "Wise Owl," "Mighty Volcano," "Curious Telescope").  This is because AI will generate images based on these titles, and we want to avoid human depictions.
* `"description"`:  A brief and vivid explanation of the card's topic (1-2 sentences). This description should also have a character-like quality, matching the style of the card title. 
* `"greenValue"`: The value of the green attribute, represented as an integer between 1 and 10 inclusive.
* `"redValue"`: The value of the red attribute, represented as an integer between 1 and 10 inclusive.
* `"blueValue"`: The value of the blue attribute, represented as an integer between 1 and 10 inclusive.
* `"imagePrompt"`: A text prompt for generating an image for the card using Imagen 2. This prompt should:
    - **Clearly depict the character from the card's "title" field.**
    - **Show this character engaged in an activity or interacting with elements that visually represent the card's theme or subject matter.**
    - **It is CRITICALLY IMPORTANT that you include the EXACT phrases "2D flat vector art" and "in the style of Bauhaus" in EVERY image prompt. This ensures consistency in the visual style of the cards, which is essential for the game's design.**
    - **If the card's content involves specialized vocabulary or complex concepts, use more universally understood, visual language to ensure the AI image model can interpret the prompt effectively.**
    
Your task is to create a JSON string representing the card stack. Each card will have specific placeholders for its content, which you will replace with actual content based on the provided visuals. 

Before providing the final output, meticulously double-check the entire JSON to ensure:

1. **All placeholders have been correctly replaced with appropriate data.**
2. **No fields are missing from any card.**
3. **The JSON structure is perfectly valid and can be parsed by a computer.**
4. **All string values within the JSON are properly escaped. This means that certain characters within the string values need to be replaced with special escape sequences. Here are the specific replacements you MUST make within the text you provide for {question}, {a}, {b}, {c}, {d}, {title}, {description}, and {imagePrompt}:**
   - **Replace `"` with `\\"`**
   - **Replace `\` with `\\\\`**
   - **Replace `/` with `\\/`**
   - **Replace backspace with `\\b`**
   - **Replace form feed with `\\f`**
   - **Replace newline with `\\n`**
   - **Replace carriage return with `\\r`**
   - **Replace tab with `\\t`**

Use the following template, replacing the placeholders (in curly braces {}) with the appropriate content, and making sure to escape the special characters in the string values as explained above:

```json
{"title":"{stackTitle}","greenName":"{greenName}","redName":"{redName}","blueName":"{blueName}","cards":[{"question":"{question}","a":"{a}","b":"{b}","c":"{c}","d":"{d}","answer":"{answer}","title":"{title}","description":"{description}","greenValue":{greenValue},"redValue":{redValue},"blueName":"{blueName}","blueValue":{blueValue}, "imagePrompt":"{imagePrompt}"},{"question":"{question}","a":"{a}","b":"{b}","c":"{c}","d":"{d}","answer":"{answer}","title":"{title}","description":"{description}","greenValue":{greenValue},"redName":"{redName}","redValue":{redValue},"blueName":"{blueName}","blueValue":{blueValue}, "imagePrompt":"{imagePrompt}"},/* ... repeat for a total of at least 70 cards ... */]}
```

Here are the rules for replacing the placeholders:

* **{stackTitle}**: The title of the card stack, either taken verbatim from a clear chapter or subject title in the screenshots or a fun, engaging and brief title you create. 
* **{greenName}, {redName}, {blueName}**: Thematic attribute names you choose for the entire stack. These names should be descriptive and represent qualities or characteristics that non-human characters (like the ones used as card titles) can have and can be measured on a scale of 1 to 10. 
* **{question}**: The text of a multiple-choice question related to the visuals.
* **{a}, {b}, {c}, {d}**: The four answer options.
* **{answer}**: The correct answer (a, b, c, or d).
* **{title}**: A non-human character name suitable for AI image generation (e.g., "Wise Owl," "Curious Telescope").
* **{description}**: A brief, character-like description of the card's topic (1-2 sentences).
* **{greenValue}, {redValue}, {blueValue}**: Integers between 1 and 10 for the attribute values.

Ensure each question:

* Covers a unique piece of information from the visuals – avoid redundancy. Only if you have exhausted all unique pieces of information from the visual materials are you allowed to create multiple questions about the same piece of information.
* Varies in difficulty, reflected in the attribute values (higher values = more difficult questions).
* Has a cohesive theme throughout the card (title, description, attributes, and question).
* Is appropriate for a student learning about the subject matter, avoiding irrelevant or overly specialized details. 
"""

const val CORRECTION_PROMPT = """
You are a meticulous teacher and JSON debugger. I attempted to generate a JSON string for a card game based on the provided visual materials (images), but it contains errors, and some fields might be missing.

YOUR PRIMARY OBJECTIVE IS TO PRODUCE A PERFECTLY VALID AND PARSABLE JSON STRING.

Your task is to analyze the JSON, understand its structure and intended content, and then fix ALL errors and fill in ALL missing fields.

IMPORTANT:

* Use the provided visual materials (images) and your understanding of the card game structure to fill in any missing fields.
* DO NOT generate entirely new cards.
* Make sure all added content is consistent with the existing card data and the overall theme.
* All string values within the JSON MUST be properly escaped using the following escape sequences:
    - Replace `"` with `\\"`
    - Replace `\` with `\\\\`
    - Replace `/` with `\\/`
    - Replace backspace with `\\b`
    - Replace form feed with `\\f`
    - Replace newline with `\\n`
    - Replace carriage return with `\\r`
    - Replace tab with `\\t`

Here is the problematic JSON string:
```
%s
```

Here is the error message I received:
```
%s
```

Here is a description of the card game structure and content:

* **Purpose:** The card game is designed to help students learn about a variety of subjects based on visual materials.
* **Question Types:** The questions on the cards should test knowledge in the following areas:
    * **Key Facts and Figures:** Questions about significant numbers, statistics, or key findings.
    * **Cause and Effect Relationships:** Questions exploring how different elements or events influence one another.
    * **Definitions and Terminology:** Questions testing understanding of important terms and concepts.
    * **Comparisons and Contrasts:** Questions that require students to identify similarities, differences, or patterns between different elements.
* **Card Structure:** 
    * The JSON will represent a card stack, with a "title" field for the stack name and a "cards" field containing an array of card objects.
    * **Global Attributes:** At the top level of the JSON, there will be three fields: `"greenName"`, `"redName"`, and `"blueName"`. These represent the names of the three attributes used for all cards in the stack.
    * Each card object must have the following fields:
        * `"question"`: The text of the multiple-choice question.
        * `"a"`, `"b"`, `"c"`, `"d"`: The four answer options for the question.
        * `"answer"`: The correct answer (a, b, c, or d).
        * `"title"`: A concise and engaging title reflecting the question's theme, sounding like a non-human character name (e.g., "Wise Owl," "Curious Telescope").
        * `"description"`: A brief, character-like description of the card's topic (1-2 sentences).
        * `"greenValue"`, `"redValue"`, `"blueValue"`: Integer values between 1 and 10 inclusive for the corresponding global attributes.
        * `"imagePrompt"`: A text prompt for generating an image for the card using Imagen 2. This prompt should:
            - **Clearly depict the character from the card's "title" field.**
            - **Show this character engaged in an activity or interacting with elements that visually represent the card's theme or subject matter.**
            - **It MUST include the EXACT phrases "2D flat vector art" and "in the style of Bauhaus" to maintain a consistent visual style for all cards.** 
            - **If the card's content involves specialized vocabulary or complex concepts, the `imagePrompt` should use more universally understood, visual language to ensure the AI image model can interpret it effectively.**

Analyze the error message, the JSON, and the visual materials. Correct the errors, fill in the missing fields, and ensure the JSON is perfectly valid. Return ONLY the corrected JSON string. 
"""