package net.schueller.peertube.feature_server_address.presentation.server_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.schueller.peertube.R
import net.schueller.peertube.feature_server_address.domain.model.Server


@Composable
fun ServerListItem(
    server: Server,
    onItemClick: (Server) -> Unit
) {
    Card(

        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onItemClick(server) },
        elevation = 12.dp

    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
        ) {

            Column() {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = server.name ?: "",
                        fontWeight = FontWeight.Bold,
                    )
                    Row(horizontalArrangement = Arrangement.End) {

                        if (server.signupAllowed) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painterResource(id = R.drawable.ic_user_plus),
                                contentDescription = "signupAllowed",
                                modifier = Modifier.requiredSize(16.dp),
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                        if (server.isNSFW) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painterResource(id = R.drawable.ic_eye_off),
                                contentDescription = "isNSFW",
                                modifier = Modifier.requiredSize(16.dp),
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                        if (server.liveEnabled) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painterResource(id = R.drawable.ic_radio),
                                contentDescription = "liveEnabled",
                                modifier = Modifier.requiredSize(16.dp),
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                    }
                }
                Text(
                    text = server.host ?: "",
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = server.country ?: "",
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = server.shortDescription ?: "",
                    fontWeight = FontWeight.Normal,
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Total Videos: " + server.totalVideos.toString() ?: "",
                        fontWeight = FontWeight.Normal,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Total Local Videos: " + server.totalLocalVideos.toString() ?: "",
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

        }

    }
}