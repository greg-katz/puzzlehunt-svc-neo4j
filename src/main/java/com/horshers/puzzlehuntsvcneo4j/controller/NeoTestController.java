package com.horshers.puzzlehuntsvcneo4j.controller;

import com.horshers.puzzlehuntsvcneo4j.dao.PuzzleHuntDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NeoTestController {

  @Autowired
  PuzzleHuntDao puzzleHuntDao;

  @RequestMapping("/any-data-test")
  public Object literallyAnyDataFromNeo() {
    return puzzleHuntDao.literallyAnyData();
  }
}
