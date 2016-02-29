package com.github.sedubois;

import com.github.sedubois.practice.presentation.PracticeController;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface App {
  PracticeController practiceController();
}
