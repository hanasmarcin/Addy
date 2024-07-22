package com.hanas.addy.ui

import com.hanas.addy.model.PlayingCardStack
import kotlinx.serialization.json.Json

val samplePlayingCardStack by lazy { Json.decodeFromString<PlayingCardStack>(STACK_JSON) }

const val STACK_JSON = """{
  "cards": [
    {
      "attributes": {
        "blue": {
          "name": "Influence on Human Life",
          "value": 8
        },
        "green": {
          "name": "Geographic Significance",
          "value": 7
        },
        "red": {
          "name": "Climate Complexity",
          "value": 5
        }
      },
      "description": "This nomadic air current is like a wandering traveler, bringing life to the lands it touches, but leaving behind a trail of change and challenges.",
      "question": {
        "a": "10%",
        "answer": "c",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What percentage of Asia's total land area is occupied by countries with a monsoon climate?"
      },
      "title": "The Wandering Monsoon"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Significance",
          "value": 7
        },
        "green": {
          "name": "Food Security",
          "value": 8
        },
        "red": {
          "name": "Global Impact",
          "value": 6
        }
      },
      "description": "This humble grain holds the power to feed vast populations, weaving together cultures and economies across Asia.",
      "question": {
        "a": "India",
        "answer": "d",
        "b": "China",
        "c": "Vietnam",
        "d": "Japan",
        "text": "Which of these countries is NOT included among the top 10 rice producers worldwide?"
      },
      "title": "The Rice Whisperer"
    },
    {
      "attributes": {
        "blue": {
          "name": "Geographical Impact",
          "value": 6
        },
        "green": {
          "name": "Atmospheric Processes",
          "value": 9
        },
        "red": {
          "name": "Climate Influence",
          "value": 7
        }
      },
      "description": "These mighty winds are like the threads that bind Asia together, shaping the land, the people, and the lives they lead.",
      "question": {
        "a": "The rotation of the Earth",
        "answer": "c",
        "b": "The Earth's tilt on its axis",
        "c": "The difference in temperature between land and sea",
        "d": "The melting of glaciers",
        "text": "What is the primary cause of the monsoons in South and East Asia?"
      },
      "title": "The Wind Weaver"
    },
    {
      "attributes": {
        "blue": {
          "name": "Economic Impact",
          "value": 6
        },
        "green": {
          "name": "Food Preservation",
          "value": 7
        },
        "red": {
          "name": "Technological Advancement",
          "value": 5
        }
      },
      "description": "A watchful eye over the bounty of the land, ensuring that the hard work of farmers is not lost, but used to feed the many.",
      "question": {
        "a": "Improving storage facilities",
        "answer": "d",
        "b": "Reducing food waste",
        "c": "Utilizing advanced technology",
        "d": "Focusing on sustainable farming practices",
        "text": "Which of the following is NOT a key characteristic of the 'post-harvest' system in Asia?"
      },
      "title": "The Harvest Guardian"
    },
    {
      "attributes": {
        "blue": {
          "name": "Economic Value",
          "value": 5
        },
        "green": {
          "name": "Agricultural Significance",
          "value": 6
        },
        "red": {
          "name": "Regional Specialization",
          "value": 4
        }
      },
      "description": "This grain, a staple food across the globe, holds a unique place in Asia's food traditions and economic landscape.",
      "question": {
        "a": "Central Asia",
        "answer": "c",
        "b": "Southeast Asia",
        "c": "South Asia",
        "d": "East Asia",
        "text": "In which of these regions does the highest amount of wheat production occur in Asia?"
      },
      "title": "The Wheat Whisperer"
    },
    {
      "attributes": {
        "blue": {
          "name": "Food Security",
          "value": 8
        },
        "green": {
          "name": "Crop Diversity",
          "value": 7
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "This land, a treasure chest of diverse crops, fuels a thriving agricultural sector, shaping the livelihoods of millions.",
      "question": {
        "a": "Rice",
        "answer": "d",
        "b": "Wheat",
        "c": "Cotton",
        "d": "Tea",
        "text": "Which of the following agricultural products is NOT a key contributor to India's overall agricultural production?"
      },
      "title": "The Bountiful Harvest"
    },
    {
      "attributes": {
        "blue": {
          "name": "Food Security",
          "value": 9
        },
        "green": {
          "name": "Global Impact",
          "value": 8
        },
        "red": {
          "name": "Agricultural Significance",
          "value": 7
        }
      },
      "description": "A watchful eye over the world's rice bowl, ensuring a stable supply of this vital grain for billions of people.",
      "question": {
        "a": "25%",
        "answer": "d",
        "b": "50%",
        "c": "75%",
        "d": "90%",
        "text": "What is the approximate percentage of the global rice production that comes from Asia?"
      },
      "title": "The Rice Guardian"
    },
    {
      "attributes": {
        "blue": {
          "name": "Economic Impact",
          "value": 5
        },
        "green": {
          "name": "Cultural Significance",
          "value": 7
        },
        "red": {
          "name": "Global Trade",
          "value": 6
        }
      },
      "description": "This ancient beverage, a symbol of tranquility and tradition, has a story that unfolds across the vast landscapes of Asia.",
      "question": {
        "a": "China",
        "answer": "a",
        "b": "India",
        "c": "Sri Lanka",
        "d": "Japan",
        "text": "Which country holds the title of being the world's largest producer of tea?"
      },
      "title": "The Tea Alchemist"
    },
    {
      "attributes": {
        "blue": {
          "name": "Environmental Impact",
          "value": 5
        },
        "green": {
          "name": "Technological Innovation",
          "value": 8
        },
        "red": {
          "name": "Economic Efficiency",
          "value": 6
        }
      },
      "description": "With tools both modern and ancient, this skilled hand works the land, shaping not just crops, but the future of agriculture.",
      "question": {
        "a": "Increased crop yields",
        "answer": "c",
        "b": "Reduced reliance on manual labor",
        "c": "Higher food prices",
        "d": "Greater environmental sustainability",
        "text": "Which of the following factors is NOT typically associated with the use of modern agricultural technology in Asia?"
      },
      "title": "The Tech-Savvy Farmer"
    },
    {
      "attributes": {
        "blue": {
          "name": "Technological Advancement",
          "value": 7
        },
        "green": {
          "name": "Cultural Heritage",
          "value": 8
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "This intricate art, a testament to human ingenuity, has woven together trade routes, cultural traditions, and stories across the ages.",
      "question": {
        "a": "China",
        "answer": "a",
        "b": "India",
        "c": "Japan",
        "d": "South Korea",
        "text": "Which country is renowned for its advanced techniques in sericulture, the production of silk?"
      },
      "title": "The Silk Weaver"
    },
    {
      "attributes": {
        "blue": {
          "name": "Geographic Impact",
          "value": 7
        },
        "green": {
          "name": "Climate Influence",
          "value": 8
        },
        "red": {
          "name": "Agricultural Practices",
          "value": 6
        }
      },
      "description": "This seasonal wind, like a powerful storyteller, shapes the landscape and the lives of those who call Asia home.",
      "question": {
        "a": "Monsoon",
        "answer": "a",
        "b": "Tropical Cyclone",
        "c": "El Niño",
        "d": "La Niña",
        "text": "What is the name of the major climate factor that plays a crucial role in shaping the agricultural practices in both South and East Asia?"
      },
      "title": "The Monsoon's Whisper"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 4
        },
        "green": {
          "name": "Economic Importance",
          "value": 6
        },
        "red": {
          "name": "Global Trade",
          "value": 5
        }
      },
      "description": "This resilient plant, a silent giant, plays a vital role in the global economy, shaping lives and industries across Asia.",
      "question": {
        "a": "India",
        "answer": "b",
        "b": "China",
        "c": "Thailand",
        "d": "Vietnam",
        "text": "Which of these countries is NOT a significant contributor to global rubber production?"
      },
      "title": "The Rubber Tree Guardian"
    },
    {
      "attributes": {
        "blue": {
          "name": "Economic Value",
          "value": 5
        },
        "green": {
          "name": "Agricultural Significance",
          "value": 6
        },
        "red": {
          "name": "Regional Specialization",
          "value": 4
        }
      },
      "description": "A versatile plant, it's not just about the oil, but the vast network of people and industries it supports.",
      "question": {
        "a": "Southeast Asia",
        "answer": "a",
        "b": "South Asia",
        "c": "East Asia",
        "d": "Central Asia",
        "text": "In which of these regions is the majority of Asia's oil palm production concentrated?"
      },
      "title": "The Oil Palm Whisperer"
    },
    {
      "attributes": {
        "blue": {
          "name": "Environmental Impact",
          "value": 6
        },
        "green": {
          "name": "Agricultural Innovation",
          "value": 8
        },
        "red": {
          "name": "Economic Impact",
          "value": 7
        }
      },
      "description": "A quest for higher yields, this movement transformed the agricultural landscape of Asia, bringing about both progress and challenges.",
      "question": {
        "a": "Wheat",
        "answer": "c",
        "b": "Rice",
        "c": "Cotton",
        "d": "Soybeans",
        "text": "Which of these agricultural products is NOT typically associated with the 'Green Revolution' in Asia?"
      },
      "title": "The Green Revolution Pioneer"
    },
    {
      "attributes": {
        "blue": {
          "name": "Social Impact",
          "value": 6
        },
        "green": {
          "name": "Human Connection",
          "value": 8
        },
        "red": {
          "name": "Economic Significance",
          "value": 7
        }
      },
      "description": "These hands, skilled and weathered, nourish the land, ensuring sustenance for generations to come.",
      "question": {
        "a": "25%",
        "answer": "b",
        "b": "50%",
        "c": "75%",
        "d": "90%",
        "text": "What is the approximate percentage of Asia's population that is directly or indirectly involved in the agricultural sector?"
      },
      "title": "The Land's Keepers"
    },
    {
      "attributes": {
        "blue": {
          "name": "Economic Value",
          "value": 6
        },
        "green": {
          "name": "Agricultural Importance",
          "value": 7
        },
        "red": {
          "name": "Regional Specialization",
          "value": 5
        }
      },
      "description": "A fertile valley, where the river's flow brings life and sustenance, fostering a rich agricultural tapestry.",
      "question": {
        "a": "Wheat",
        "answer": "d",
        "b": "Rice",
        "c": "Cotton",
        "d": "Tea",
        "text": "Which of these crops is NOT a significant contributor to the livelihoods of farmers in the Indus River Valley?"
      },
      "title": "The Indus River's Bounty"
    },
    {
      "attributes": {
        "blue": {
          "name": "Environmental Impact",
          "value": 5
        },
        "green": {
          "name": "Global Significance",
          "value": 7
        },
        "red": {
          "name": "Human Impact",
          "value": 6
        }
      },
      "description": "This vast expanse, a canvas for human endeavor, provides sustenance and nourishes the world.",
      "question": {
        "a": "10%",
        "answer": "b",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What is the approximate percentage of the global land area that is devoted to agriculture?"
      },
      "title": "The Land's Steward"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 4
        },
        "green": {
          "name": "Economic Importance",
          "value": 6
        },
        "red": {
          "name": "Global Trade",
          "value": 5
        }
      },
      "description": "A soft, white fiber, spun into threads of commerce, this plant plays a crucial role in Asia's economy and culture.",
      "question": {
        "a": "India",
        "answer": "b",
        "b": "China",
        "c": "Pakistan",
        "d": "Bangladesh",
        "text": "In which country does the largest amount of cultivated land dedicated to cotton production reside?"
      },
      "title": "The Cotton Weaver"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 3
        },
        "green": {
          "name": "Economic Importance",
          "value": 5
        },
        "red": {
          "name": "Regional Specialization",
          "value": 4
        }
      },
      "description": "This tough, versatile fiber, a treasure of the Ganges delta, has woven its way into the fabric of Asian economies and lives.",
      "question": {
        "a": "Bangladesh",
        "answer": "c",
        "b": "India",
        "c": "China",
        "d": "Pakistan",
        "text": "Which of these countries is NOT a major producer of jute, a natural fiber commonly used for making sacks, carpets, and other items?"
      },
      "title": "The Jute Weaver"
    },
    {
      "attributes": {
        "blue": {
          "name": "Historical Impact",
          "value": 7
        },
        "green": {
          "name": "Cultural Significance",
          "value": 8
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "A thread of luxury and tradition, connecting civilizations across time and space, leaving a lasting mark on Asia's cultural landscape.",
      "question": {
        "a": "India",
        "answer": "d",
        "b": "China",
        "c": "Japan",
        "d": "South Korea",
        "text": "Which of these countries is NOT known for its production of silk, a luxurious fabric prized for its delicate texture and sheen?"
      },
      "title": "The Silk Road's Legacy"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 4
        },
        "green": {
          "name": "Global Trade",
          "value": 6
        },
        "red": {
          "name": "Economic Importance",
          "value": 5
        }
      },
      "description": "A journey of flavor and culture, this beverage, born in the highlands of Asia, has captivated the world with its aroma and taste.",
      "question": {
        "a": "Vietnam",
        "answer": "d",
        "b": "India",
        "c": "Indonesia",
        "d": "Thailand",
        "text": "Which of these countries is NOT a major producer of coffee, a popular beverage enjoyed around the world?"
      },
      "title": "The Coffee Bean Explorer"
    },
    {
      "attributes": {
        "blue": {
          "name": "Economic Efficiency",
          "value": 6
        },
        "green": {
          "name": "Cultural Significance",
          "value": 8
        },
        "red": {
          "name": "Environmental Sustainability",
          "value": 7
        }
      },
      "description": "A tapestry of knowledge, passed down through generations, these practices reflect a deep understanding of the land and its resources.",
      "question": {
        "a": "Lack of access to modern technology",
        "answer": "d",
        "b": "Preservation of cultural heritage",
        "c": "Environmental sustainability",
        "d": "All of the above",
        "text": "What is the primary reason behind the adoption of traditional agricultural practices in many parts of Asia?"
      },
      "title": "The Guardians of Tradition"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 4
        },
        "green": {
          "name": "Economic Importance",
          "value": 6
        },
        "red": {
          "name": "Global Trade",
          "value": 5
        }
      },
      "description": "This sweet, versatile plant, a source of both food and fuel, plays a key role in Asia's economy and culture.",
      "question": {
        "a": "India",
        "answer": "b",
        "b": "China",
        "c": "Brazil",
        "d": "Thailand",
        "text": "Which of these countries is NOT a significant contributor to global sugarcane production?"
      },
      "title": "The Sugarcane Whisperer"
    },
    {
      "attributes": {
        "blue": {
          "name": "Human Impact",
          "value": 6
        },
        "green": {
          "name": "Environmental Significance",
          "value": 8
        },
        "red": {
          "name": "Biodiversity",
          "value": 7
        }
      },
      "description": "A vast expanse of green, teeming with life, these forests play a vital role in Asia's ecosystem and the lives of its people.",
      "question": {
        "a": "10%",
        "answer": "b",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What is the approximate percentage of Asia's total land area that is covered by forests?"
      },
      "title": "The Forest's Embrace"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Significance",
          "value": 5
        },
        "green": {
          "name": "Food Security",
          "value": 7
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "From the pastures to the plates, these animals play a vital role in the food and economy of many Asian nations.",
      "question": {
        "a": "China",
        "answer": "c",
        "b": "India",
        "c": "Japan",
        "d": "Australia",
        "text": "Which of these countries is NOT a major producer of livestock, including cattle, pigs, sheep, and poultry?"
      },
      "title": "The Livestock Keeper"
    },
    {
      "attributes": {
        "blue": {
          "name": "Human Impact",
          "value": 7
        },
        "green": {
          "name": "Agricultural Significance",
          "value": 8
        },
        "red": {
          "name": "Environmental Impact",
          "value": 6
        }
      },
      "description": "A network of ingenuity, these systems harness the life-giving power of water, transforming landscapes and livelihoods.",
      "question": {
        "a": "To ensure a consistent water supply for agriculture",
        "answer": "d",
        "b": "To protect against drought and water scarcity",
        "c": "To improve crop yields and productivity",
        "d": "All of the above",
        "text": "What is the primary reason for the widespread adoption of irrigation systems in Asia?"
      },
      "title": "The Water Weaver"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 4
        },
        "green": {
          "name": "Global Trade",
          "value": 6
        },
        "red": {
          "name": "Economic Importance",
          "value": 5
        }
      },
      "description": "A vibrant rainbow of flavors, these crops are not just food, but also a source of income and livelihood for millions in Asia.",
      "question": {
        "a": "China",
        "answer": "d",
        "b": "India",
        "c": "Thailand",
        "d": "South Korea",
        "text": "Which of these countries is NOT a leading exporter of fruits and vegetables?"
      },
      "title": "The Fruit and Vegetable Basket"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 6
        },
        "green": {
          "name": "Social Significance",
          "value": 8
        },
        "red": {
          "name": "Economic Importance",
          "value": 7
        }
      },
      "description": "A symphony of life, where tradition meets progress, these rural communities form the backbone of Asia's agricultural landscape.",
      "question": {
        "a": "25%",
        "answer": "c",
        "b": "50%",
        "c": "75%",
        "d": "90%",
        "text": "What is the approximate percentage of Asia's population that lives in rural areas?"
      },
      "title": "The Rural Heartbeat"
    },
    {
      "attributes": {
        "blue": {
          "name": "Social Impact",
          "value": 6
        },
        "green": {
          "name": "Environmental Sustainability",
          "value": 8
        },
        "red": {
          "name": "Economic Challenges",
          "value": 7
        }
      },
      "description": "A balancing act, finding ways to feed a growing population while respecting the limits of the land, is a key challenge for Asia's future.",
      "question": {
        "a": "Climate change",
        "answer": "d",
        "b": "Population growth",
        "c": "Lack of access to modern technology",
        "d": "High literacy rates",
        "text": "Which of these factors is NOT a major challenge facing the agricultural sector in Asia?"
      },
      "title": "The Challenges of Abundance"
    },
    {
      "attributes": {
        "blue": {
          "name": "Environmental Impact",
          "value": 5
        },
        "green": {
          "name": "Global Significance",
          "value": 7
        },
        "red": {
          "name": "Human Impact",
          "value": 6
        }
      },
      "description": "A vast canvas for human endeavor, this land nourishes the world, providing sustenance and livelihoods for billions.",
      "question": {
        "a": "10%",
        "answer": "c",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What is the approximate percentage of Asia's total land area that is used for agricultural purposes?"
      },
      "title": "The Land's Provider"
    },
    {
      "attributes": {
        "blue": {
          "name": "Environmental Impact",
          "value": 5
        },
        "green": {
          "name": "Economic Significance",
          "value": 7
        },
        "red": {
          "name": "Social Impact",
          "value": 6
        }
      },
      "description": "A continent on the move, this growing population demands food, placing significant pressure on agricultural systems and resources.",
      "question": {
        "a": "Rapid population growth",
        "answer": "d",
        "b": "Rising incomes",
        "c": "Shifting dietary preferences",
        "d": "Decreasing urbanization",
        "text": "Which of the following is NOT a major factor driving the demand for food in Asia?"
      },
      "title": "The Hungry Giant"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 6
        },
        "green": {
          "name": "Social Significance",
          "value": 8
        },
        "red": {
          "name": "Economic Importance",
          "value": 7
        }
      },
      "description": "These hands, skilled and weathered, till the land, nurturing life and ensuring sustenance for millions.",
      "question": {
        "a": "25%",
        "answer": "a",
        "b": "50%",
        "c": "75%",
        "d": "90%",
        "text": "What is the approximate percentage of Asia's workforce that is employed in the agricultural sector?"
      },
      "title": "The Farmers' Hands"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 5
        },
        "green": {
          "name": "Global Trade",
          "value": 7
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "A treasure trove of diverse crops, Asia feeds not just its own people, but also millions across the globe.",
      "question": {
        "a": "China",
        "answer": "d",
        "b": "India",
        "c": "Vietnam",
        "d": "South Korea",
        "text": "Which of the following countries is NOT a major exporter of agricultural products?"
      },
      "title": "The Food Basket of the World"
    },
    {
      "attributes": {
        "blue": {
          "name": "Human Impact",
          "value": 7
        },
        "green": {
          "name": "Agricultural Significance",
          "value": 8
        },
        "red": {
          "name": "Environmental Impact",
          "value": 6
        }
      },
      "description": "A network of ingenuity, these systems harness the life-giving power of water, transforming landscapes and livelihoods.",
      "question": {
        "a": "10%",
        "answer": "c",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What is the approximate percentage of Asia's total agricultural land that is irrigated?"
      },
      "title": "The Water's Embrace"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 6
        },
        "green": {
          "name": "Global Significance",
          "value": 8
        },
        "red": {
          "name": "Economic Importance",
          "value": 7
        }
      },
      "description": "A symbol of sustenance and culture, this grain plays a crucial role in Asia's food and economy.",
      "question": {
        "a": "China",
        "answer": "d",
        "b": "India",
        "c": "Thailand",
        "d": "Australia",
        "text": "Which of these countries is NOT a major producer of rice, a staple food for millions in Asia?"
      },
      "title": "The Rice Bowl"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 5
        },
        "green": {
          "name": "Global Trade",
          "value": 7
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "From the highlands of Asia, this beloved beverage has traveled the world, leaving a trail of flavor and culture.",
      "question": {
        "a": "China",
        "answer": "d",
        "b": "India",
        "c": "Sri Lanka",
        "d": "Thailand",
        "text": "Which of these countries is NOT a major exporter of tea, a beverage enjoyed worldwide?"
      },
      "title": "The Tea Trade Route"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 4
        },
        "green": {
          "name": "Economic Importance",
          "value": 6
        },
        "red": {
          "name": "Global Trade",
          "value": 5
        }
      },
      "description": "A resilient plant, a silent giant, plays a vital role in the global economy, shaping lives and industries across Asia.",
      "question": {
        "a": "Thailand",
        "answer": "d",
        "b": "India",
        "c": "Indonesia",
        "d": "South Korea",
        "text": "Which of these countries is NOT a significant contributor to the global production of rubber, a versatile material used in various industries?"
      },
      "title": "The Rubber Tree's Legacy"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 4
        },
        "green": {
          "name": "Economic Importance",
          "value": 6
        },
        "red": {
          "name": "Global Trade",
          "value": 5
        }
      },
      "description": "A treasure trove of versatility, this oil has a wide range of applications, from food to biofuels, making it a key commodity in Asia's economy.",
      "question": {
        "a": "Indonesia",
        "answer": "c",
        "b": "Malaysia",
        "c": "India",
        "d": "Thailand",
        "text": "Which of these countries is NOT a major producer of palm oil, a versatile oil derived from the fruit of the oil palm tree?"
      },
      "title": "The Oil Palm's Bounty"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 4
        },
        "green": {
          "name": "Global Trade",
          "value": 6
        },
        "red": {
          "name": "Economic Importance",
          "value": 5
        }
      },
      "description": "A journey of flavor and culture, this beverage, born in the highlands of Asia, has captivated the world with its aroma and taste.",
      "question": {
        "a": "Vietnam",
        "answer": "d",
        "b": "Brazil",
        "c": "Indonesia",
        "d": "South Korea",
        "text": "Which of these countries is NOT a significant contributor to the global production of coffee, a popular beverage enjoyed worldwide?"
      },
      "title": "The Coffee Bean's Journey"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 4
        },
        "green": {
          "name": "Economic Importance",
          "value": 6
        },
        "red": {
          "name": "Global Trade",
          "value": 5
        }
      },
      "description": "A sweet, versatile plant, a source of both food and fuel, plays a key role in Asia's economy and culture.",
      "question": {
        "a": "India",
        "answer": "b",
        "b": "China",
        "c": "Brazil",
        "d": "Thailand",
        "text": "Which of these countries is NOT a major producer of sugarcane, a versatile crop used for making sugar, molasses, and other products?"
      },
      "title": "The Sugarcane's Sweetness"
    },
    {
      "attributes": {
        "blue": {
          "name": "Industrial Significance",
          "value": 3
        },
        "green": {
          "name": "Economic Importance",
          "value": 5
        },
        "red": {
          "name": "Regional Specialization",
          "value": 4
        }
      },
      "description": "This tough, versatile fiber, a treasure of the Ganges delta, has woven its way into the fabric of Asian economies and lives.",
      "question": {
        "a": "Bangladesh",
        "answer": "c",
        "b": "India",
        "c": "China",
        "d": "Pakistan",
        "text": "Which of these countries is NOT a major producer of jute, a natural fiber commonly used for making sacks, carpets, and other items?"
      },
      "title": "The Jute's Strength"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Significance",
          "value": 5
        },
        "green": {
          "name": "Food Security",
          "value": 7
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "From the pastures to the plates, these animals play a vital role in the food and economy of many Asian nations.",
      "question": {
        "a": "China",
        "answer": "c",
        "b": "India",
        "c": "Japan",
        "d": "Australia",
        "text": "Which of these countries is NOT a major producer of livestock, including cattle, pigs, sheep, and poultry?"
      },
      "title": "The Livestock's Role"
    },
    {
      "attributes": {
        "blue": {
          "name": "Human Impact",
          "value": 6
        },
        "green": {
          "name": "Environmental Significance",
          "value": 8
        },
        "red": {
          "name": "Biodiversity",
          "value": 7
        }
      },
      "description": "A vast expanse of green, teeming with life, these forests play a vital role in Asia's ecosystem and the lives of its people.",
      "question": {
        "a": "10%",
        "answer": "b",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What is the approximate percentage of Asia's total land area that is covered by forests?"
      },
      "title": "The Forest's Guardian"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 6
        },
        "green": {
          "name": "Social Significance",
          "value": 8
        },
        "red": {
          "name": "Economic Importance",
          "value": 7
        }
      },
      "description": "A symphony of life, where tradition meets progress, these rural communities form the backbone of Asia's agricultural landscape.",
      "question": {
        "a": "25%",
        "answer": "c",
        "b": "50%",
        "c": "75%",
        "d": "90%",
        "text": "What is the approximate percentage of Asia's population that lives in rural areas?"
      },
      "title": "The Rural Tapestry"
    },
    {
      "attributes": {
        "blue": {
          "name": "Environmental Impact",
          "value": 5
        },
        "green": {
          "name": "Global Significance",
          "value": 7
        },
        "red": {
          "name": "Human Impact",
          "value": 6
        }
      },
      "description": "A vast canvas for human endeavor, this land nourishes the world, providing sustenance and livelihoods for billions.",
      "question": {
        "a": "10%",
        "answer": "c",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What is the approximate percentage of Asia's total land area that is used for agricultural purposes?"
      },
      "title": "The Land's Bounty"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 5
        },
        "green": {
          "name": "Global Trade",
          "value": 7
        },
        "red": {
          "name": "Economic Importance",
          "value": 6
        }
      },
      "description": "A treasure trove of diverse crops, Asia feeds not just its own people, but also millions across the globe.",
      "question": {
        "a": "China",
        "answer": "d",
        "b": "India",
        "c": "Vietnam",
        "d": "South Korea",
        "text": "Which of these countries is NOT a major exporter of agricultural products?"
      },
      "title": "The Food Trader"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 4
        },
        "green": {
          "name": "Global Trade",
          "value": 6
        },
        "red": {
          "name": "Economic Importance",
          "value": 5
        }
      },
      "description": "A vibrant rainbow of flavors, these crops are not just food, but also a source of income and livelihood for millions in Asia.",
      "question": {
        "a": "China",
        "answer": "d",
        "b": "India",
        "c": "Thailand",
        "d": "South Korea",
        "text": "Which of these countries is NOT a leading exporter of fruits and vegetables?"
      },
      "title": "The Fruit and Vegetable Market"
    },
    {
      "attributes": {
        "blue": {
          "name": "Cultural Influence",
          "value": 6
        },
        "green": {
          "name": "Social Significance",
          "value": 8
        },
        "red": {
          "name": "Economic Importance",
          "value": 7
        }
      },
      "description": "These hands, skilled and weathered, till the land, nurturing life and ensuring sustenance for millions.",
      "question": {
        "a": "25%",
        "answer": "a",
        "b": "50%",
        "c": "75%",
        "d": "90%",
        "text": "What is the approximate percentage of Asia's workforce that is employed in the agricultural sector?"
      },
      "title": "The Farming Community"
    },
    {
      "attributes": {
        "blue": {
          "name": "Human Impact",
          "value": 7
        },
        "green": {
          "name": "Agricultural Significance",
          "value": 8
        },
        "red": {
          "name": "Environmental Impact",
          "value": 6
        }
      },
      "description": "A network of ingenuity, these systems harness the life-giving power of water, transforming landscapes and livelihoods.",
      "question": {
        "a": "10%",
        "answer": "c",
        "b": "25%",
        "c": "50%",
        "d": "75%",
        "text": "What is the approximate percentage of Asia's total agricultural land that is irrigated?"
      },
      "title": "The Water's Gift"
    }
  ],
  "createdBy": "cfs0a81YlWMJeTB1eU1QlUHCYgB3",
  "creationTimestamp": 1721315259395,
  "id": null,
  "title": "The Emerald Dragon"
}
"""
