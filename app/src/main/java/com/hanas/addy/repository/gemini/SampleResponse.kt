package com.hanas.addy.repository.gemini

import com.hanas.addy.model.PlayCardStackDTO
import com.hanas.addy.view.createNewCardStack.mapToPlayCardStack
import kotlinx.serialization.json.Json

const val SAMPLE_RESPONSE = """
{
    "title": "Asian Agriculture",
    "greenName": "Global Impact",
    "redName": "Technological Advancement",
    "blueName": "Geographic Diversity",
    "cards": [
    {
        "question": "What percentage of the world's rice production originates in Asia?",
        "a": "50%",
        "b": "65%",
        "c": "80%",
        "d": "90%",
        "answer": "d",
        "title": "Rice Paddy",
        "description": "Rice Paddy observes the world, noticing the flow of crops and the hands that tend them.",
        "greenValue": 8,
        "redValue": 3,
        "blueValue": 6,
        "imagePrompt": "abc"
    },
    {
        "question": "Which two Asian countries are highlighted for their significant role in global agricultural production?",
        "a": "Japan and South Korea",
        "b": "China and India",
        "c": "Turkey and Pakistan",
        "d": "Sri Lanka and Indonesia",
        "answer": "b",
        "title": "Diligent Farmer",
        "description": "Diligent Farmer, with calloused hands and a straw hat, knows the value of hard work and the bounty of the earth.",
        "greenValue": 9,
        "redValue": 2,
        "blueValue": 5,
        "imagePrompt": "abc"
    },
    {
        "question": "What is a significant characteristic of the climate in many parts of Asia?",
        "a": "Consistently low temperatures",
        "b": "Extreme dryness and lack of rainfall",
        "c": "Significant seasonal variations in temperature and rainfall",
        "d": "Moderate temperatures year-round",
        "answer": "c",
        "title": "Weather Watcher",
        "description": "Weather Watcher, a spirit of the sky, observes the ever-changing patterns of wind and rain.",
        "greenValue": 4,
        "redValue": 1,
        "blueValue": 7,
        "imagePrompt": "abc"
    },
    {
        "question": "What factor has driven the need for advancements in agricultural technology in some parts of Asia?",
        "a": "The desire to export more crops",
        "b": "The need to overcome challenging environmental conditions",
        "c": "The lack of traditional farming knowledge",
        "d": "The increasing popularity of organic farming methods",
        "answer": "b",
        "title": "Ingenious Inventor",
        "description": "Ingenious Inventor, a tinkerer at heart, seeks solutions to the challenges faced by those who work the land.",
        "greenValue": 5,
        "redValue": 8,
        "blueValue": 6,
        "imagePrompt": "abc"
    },
    {
        "question": "Which country is known for its traditional methods of agriculture, including the use of oxen for plowing?",
        "a": "Israel",
        "b": "Japan",
        "c": "China",
        "d": "India",
        "answer": "d",
        "title": "Oxen Team",
        "description": "Oxen Team, strong and steady, represent the traditional methods passed down through generations.",
        "greenValue": 3,
        "redValue": 2,
        "blueValue": 5,
        "imagePrompt": "abc"
    },
    {
        "question": "What is an example of how modern technology is being used in Asian agriculture?",
        "a": "Employing robots to harvest crops",
        "b": "Using drones to monitor crop health",
        "c": "Developing genetically modified seeds",
        "d": "All of the above",
        "answer": "d",
        "title": "Tech-Savvy Scarecrow",
        "description": "Tech-Savvy Scarecrow, with circuits for brains and sensors for eyes, embraces the fusion of technology and tradition.",
        "greenValue": 6,
        "redValue": 9,
        "blueValue": 7,
        "imagePrompt": "abc"
    },
    {
        "question": "What percentage of global cotton production originates in India?",
        "a": "25%",
        "b": "50%",
        "c": "75%",
        "d": "90%",
        "answer": "a",
        "title": "Cotton Blossom",
        "description": "Cotton Blossom, delicate yet resilient, symbolizes the importance of this crop to the region.",
        "greenValue": 7,
        "redValue": 3,
        "blueValue": 4,
        "imagePrompt": "abc"
    },
    {
        "question": "Which Asian country is the leading producer of tea?",
        "a": "India",
        "b": "China",
        "c": "Sri Lanka",
        "d": "Indonesia",
        "answer": "b",
        "title": "Tea Leaf",
        "description": "Tea Leaf, steeped in tradition, represents the cultural significance of this beloved beverage.",
        "greenValue": 8,
        "redValue": 2,
        "blueValue": 5,
        "imagePrompt": "abc"
    },
    {
        "question": "What percentage of the world's milk production comes from water buffalo?",
        "a": "20%",
        "b": "50%",
        "c": "70%",
        "d": "90%",
        "answer": "b",
        "title": "Water Buffalo",
        "description": "Water Buffalo, a gentle giant, provides sustenance and resources for countless communities.",
        "greenValue": 6,
        "redValue": 1,
        "blueValue": 4,
        "imagePrompt": "abc"
    },
    {
        "question": "What is a 'plon' in the context of agricultural production?",
        "a": "A type of irrigation system",
        "b": "A unit of land measurement",
        "c": "A type of fertilizer",
        "d": "A harvesting tool",
        "answer": "b",
        "title": "Land Surveyor",
        "description": "Land Surveyor, with their maps and instruments, measures the earth's bounty and the efforts of those who cultivate it.",
        "greenValue": 2,
        "redValue": 1,
        "blueValue": 3,
        "imagePrompt": "abc"
    },
    {
        "question": "What does the term 'zbiory' refer to in the context of agriculture?",
        "a": "The type of crop being grown",
        "b": "The amount of a crop harvested",
        "c": "The tools used for harvesting",
        "d": "The process of planting seeds",
        "answer": "b",
        "title": "Harvest Moon",
        "description": "Harvest Moon, full and bright, illuminates the culmination of a season's hard work.",
        "greenValue": 4,
        "redValue": 2,
        "blueValue": 3,
        "imagePrompt": "abc"
    },
    {
        "question": "According to the provided materials, which of the following is NOT a major agricultural product of Asia?",
        "a": "Rice",
        "b": "Cotton",
        "c": "Wheat",
        "d": "Coffee",
        "answer": "d",
        "title": "Crop Field",
        "description": "Crop Field, a patchwork of colors and textures, represents the diversity of Asia's agricultural landscape.",
        "greenValue": 5,
        "redValue": 1,
        "blueValue": 7,
        "imagePrompt": "abc"
    },
    {
        "question": "What factor has significantly influenced the development of specialized agricultural practices in different parts of Asia?",
        "a": "Government policies",
        "b": "Technological advancements",
        "c": "Cultural preferences",
        "d": "Geographical variations",
        "answer": "d",
        "title": "Mountain Range",
        "description": "Mountain Range, a majestic barrier, shapes the land and influences the lives of those who call it home.",
        "greenValue": 3,
        "redValue": 2,
        "blueValue": 8,
        "imagePrompt": "abc"
    },
    {
        "question": "What challenge do farmers in some arid regions of Asia face?",
        "a": "Excessive rainfall",
        "b": "Limited water resources",
        "c": "Pest infestations",
        "d": "Lack of fertile soil",
        "answer": "b",
        "title": "Desert Oasis",
        "description": "Desert Oasis, a precious source of life, highlights the challenges of arid environments.",
        "greenValue": 4,
        "redValue": 3,
        "blueValue": 6,
        "imagePrompt": "abc"
    },
    {
        "question": "What is one way that Asian countries are addressing the challenge of food security?",
        "a": "Importing food from other countries",
        "b": "Investing in agricultural research and development",
        "c": "Reducing population growth",
        "d": "Encouraging people to adopt vegetarian diets",
        "answer": "b",
        "title": "Seed of Innovation",
        "description": "Seed of Innovation, small but mighty, represents the potential for growth and progress in agriculture.",
        "greenValue": 7,
        "redValue": 8,
        "blueValue": 5,
        "imagePrompt": "abc"
    }
    ]
}
"""

val samplePlayCardStackJsonResponse = Json.decodeFromString<PlayCardStackDTO>(SAMPLE_RESPONSE)
val samplePlayCardStack = samplePlayCardStackJsonResponse
    .mapToPlayCardStack("abc")
    .let {
        it.copy(cards = it.cards.mapIndexed { id, data ->
            data.copy(id = id.toLong())
        })
    }