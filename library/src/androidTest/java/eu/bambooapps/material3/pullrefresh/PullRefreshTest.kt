package eu.bambooapps.material3.pullrefresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalMaterial3Api::class)
@RunWith(AndroidJUnit4::class)
class PullRefreshTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showProgress() {
        composeTestRule.setContent {
            Box {
                val state = rememberPullRefreshState(
                    refreshing = true,
                    onRefresh = {}
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .pullRefresh(state = state)
                ) {
                    Text(text = "Test")
                }
                PullRefreshIndicator(
                    refreshing = true,
                    state = state,
                    modifier = Modifier.semantics { testTag = INDICATOR_TAG }
                )
            }
        }
        composeTestRule.onNodeWithTag(INDICATOR_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun swipeToShowProgress() {
        composeTestRule.setContent {
            Box {
                var isRefreshing by remember {
                    mutableStateOf(false)
                }
                val state = rememberPullRefreshState(
                    refreshing = isRefreshing,
                    onRefresh = { isRefreshing = true }
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .pullRefresh(state = state)
                        .semantics { testTag = LIST_TAG }
                ) {
                    Text(text = "Test")
                }
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = state,
                    modifier = Modifier.semantics { testTag = INDICATOR_TAG }
                )
            }
        }
        composeTestRule.onNodeWithTag(LIST_TAG)
            .performTouchInput {
                swipeDown()
            }

        composeTestRule.onNodeWithTag(INDICATOR_TAG)
            .assertIsDisplayed()
    }

    companion object {
        private const val INDICATOR_TAG = "PullRefreshIndicator"
        private const val LIST_TAG = "PullRefreshList"
    }
}
