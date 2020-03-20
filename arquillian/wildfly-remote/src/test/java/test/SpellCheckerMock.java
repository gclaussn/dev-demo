package test;

import javax.enterprise.context.ApplicationScoped;

import example.spellcheck.SpellChecker;

@Mock
@ApplicationScoped
public class SpellCheckerMock implements SpellChecker {

  @Override
  public String check(String text) {
    // Mark text as spell checked
    return String.format("%s (spell checked by mock)", text);
  }
}
