package com.horshers.puzzlehuntsvcneo4j.controller;

import com.horshers.puzzlehuntsvcneo4j.dao.PuzzleHuntDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NeoTestController {

  @Autowired
  PuzzleHuntDao puzzleHuntDao;

  @RequestMapping("/any-data-test")
  @ResponseBody
  public Object literallyAnyDataFromNeo() {
    return puzzleHuntDao.literallyAnyData();
  }
}
