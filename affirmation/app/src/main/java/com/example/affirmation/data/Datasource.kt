package com.example.affirmation.data

import com.example.affirmation.R
import com.example.affirmation.model.Affirmation

class Datasource {
    fun loadAffirmations(): List<Affirmation> {
        return listOf(
            Affirmation(R.string.affirmation1, R.drawable.image1_jpg),
            Affirmation(R.string.affirmation2, R.drawable.image2_jpg),
            Affirmation(R.string.affirmation3, R.drawable.image3_jpg),
            Affirmation(R.string.affirmation4, R.drawable.image4_jpg),
            Affirmation(R.string.affirmation5, R.drawable.image5_jpg),
            Affirmation(R.string.affirmation6, R.drawable.image6_jpg),
            Affirmation(R.string.affirmation7, R.drawable.image7_jpg),
            Affirmation(R.string.affirmation8, R.drawable.image8_jpg),
            Affirmation(R.string.affirmation9, R.drawable.image9_jpg),
            Affirmation(R.string.affirmation10, R.drawable.image10_jpg)
        )
    }
}
