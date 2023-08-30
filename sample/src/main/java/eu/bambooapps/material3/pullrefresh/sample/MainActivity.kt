package eu.bambooapps.material3.pullrefresh.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
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
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Material3PullRefreshTheme {
                val isRefreshing by remember {
                    mutableStateOf(true)
                }
                val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {})
                Box {
                    LazyColumn(
                        modifier = Modifier.pullRefresh(state),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(listOf("test 1", "test 2")) {
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Item $it", modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                    PullRefreshIndicator(refreshing = isRefreshing, state = state,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    Material3PullRefreshTheme {
        Greeting("Android")
    }
}
