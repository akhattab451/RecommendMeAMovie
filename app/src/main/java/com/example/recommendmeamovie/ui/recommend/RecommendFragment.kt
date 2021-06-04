package com.example.recommendmeamovie.ui.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.recommendmeamovie.R
import com.example.recommendmeamovie.databinding.RecommendFragmentBinding
import com.example.recommendmeamovie.source.local.MovieDatabase
import com.example.recommendmeamovie.source.local.QuestionEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecommendFragment : Fragment(R.layout.recommend_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = RecommendFragmentBinding.bind(view)
        val viewModel: RecommendViewModel by viewModels()

    }

}


