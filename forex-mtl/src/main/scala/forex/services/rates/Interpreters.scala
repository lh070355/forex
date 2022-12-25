package forex.services.rates

import cats.Applicative
import cats.effect._
import forex.config.OneFrameClientConfig
import interpreters._
import org.http4s.client.Client

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F]()
  def live[F[_]: Async](oneFrameClientConfig: OneFrameClientConfig, client: Client[F]): Algebra[F] = 
    new OneFrameLive[F](oneFrameClientConfig, client)
}
