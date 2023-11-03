package eu.bambooapps.material3.pullrefresh.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import eu.bambooapps.material3.pullrefresh.sample.ui.theme.Material3PullRefreshTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Material3PullRefreshTheme {
                val isRefreshing by remember {
                    mutableStateOf(true)
                }
                PullRefreshSample(
                    isRefreshing = isRefreshing,
                    modifier = Modifier.systemBarsPadding()
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PullRefreshSample(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {})
        LazyColumn(
            modifier = Modifier.pullRefresh(state),
        ) {
            items(count = 100) {
                ListItem(headlineContent = {
                    Text(text = "Item $it", modifier = Modifier.padding(16.dp))
                })
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing, state = state,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PullRefreshSamplePreview() {
    Material3PullRefreshTheme {
        PullRefreshSample(isRefreshing = true)
    }
}
