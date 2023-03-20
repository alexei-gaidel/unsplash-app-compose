package com.example.imaginarium.compose

import com.example.imaginarium.R

class OnBoardingItems(
    val image: Int,
    val desc: Int
) {
    companion object{
        fun getData(): List<OnBoardingItems>{
            return listOf(
                OnBoardingItems(R.drawable.ellipse_one_n, R.string.onboarding_text_one),
                OnBoardingItems(R.drawable.ellipse_two_n, R.string.onboarding_text_two),
                OnBoardingItems(R.drawable.ellipse_three, R.string.onboarding_text_three)
            )
        }
    }
}