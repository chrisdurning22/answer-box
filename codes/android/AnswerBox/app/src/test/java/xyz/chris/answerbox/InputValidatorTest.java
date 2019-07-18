package xyz.cathal.answerbox;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Cathal Conroy
 */

public class InputValidatorTest {

    /*
     * isEmailValid
     */
    @Test
    public void isEmailValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isEmailValid("test@example.com"));
        assertTrue(InputValidator.isEmailValid("a@b.co.uk"));
        assertTrue(InputValidator.isEmailValid("john.f.kennedy@whitehouse.gov"));
        assertTrue(InputValidator.isEmailValid("abc-123@four56.org"));
    }

    @Test
    public void isEmailValid_Incorrect_ReturnsFalse() {
        assertFalse(InputValidator.isEmailValid("test@example#.com"));
        assertFalse(InputValidator.isEmailValid("t[e]st@example.com"));
        assertFalse(InputValidator.isEmailValid("@motorola@example.com"));
        assertFalse(InputValidator.isEmailValid("l+quid=@;.com"));
    }

    /*
     * isUsernameValid
     */
    @Test
    public void isUsernameValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isUsernameValid("abc"));
        assertTrue(InputValidator.isUsernameValid("abc123"));
        assertTrue(InputValidator.isUsernameValid("abc_123"));
        assertTrue(InputValidator.isUsernameValid("test_number_4444"));
    }

    @Test
    public void isUsernameValid_Incorrect_ReturnsFalse() {
        assertFalse(InputValidator.isUsernameValid("a")); // too short
        assertFalse(InputValidator.isUsernameValid("123abc")); // first char not letter
        assertFalse(InputValidator.isUsernameValid("_abc123")); // first char not letter
        assertFalse(InputValidator.isUsernameValid("test_number_44448888h")); // too long
    }

    /*
     * isPasswordValid
     */
    @Test
    public void isPasswordValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isPasswordValid("8Ac8'@daw"));
        assertTrue(InputValidator.isPasswordValid("%&*jfFe5pf#"));
        assertTrue(InputValidator.isPasswordValid("*&NU2iufib@}"));
        assertTrue(InputValidator.isPasswordValid("%^*nfj6nbY#'a"));
        assertTrue(InputValidator.isPasswordValid("Niall69!"));
    }

    @Test
    public void isPasswordValid_Incorrect_ReturnsFalse() {
        assertFalse(InputValidator.isPasswordValid("abCfnf*")); // too short
        assertFalse(InputValidator.isPasswordValid("ahdka&*&*")); // no upper case
        assertFalse(InputValidator.isPasswordValid("A*&BISFDS")); // no lower case
        assertFalse(InputValidator.isPasswordValid("AUHIUbfhbfhsA")); // no special chars
    }

    /*
     * isTitleValid
     */
    @Test
    public void isTitleValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isTitleValid("This is a valid Title!"));
        assertTrue(InputValidator.isTitleValid("Another similarly valid 93qhr9utitel !"));
    }

    @Test
    public void isTitleValid_inCorrect_ReturnsFalse() {
        assertFalse(InputValidator.isTitleValid("22")); // too short
    }

    /*
     * isBodyValid
     */
    @Test
    public void isBodyValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isBodyValid("valid body"));
        assertTrue(InputValidator.isBodyValid("v"));
    }

    @Test
    public void isBodyValid_inCorrect_ReturnsFalse() {
        assertFalse(InputValidator.isBodyValid("")); // too short
    }
}
