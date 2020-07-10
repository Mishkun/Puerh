package io.github.mishkun.puerh.interpreter

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DummyExecSpec : StringSpec({
    "just a dummy test to setup github actions" {
        2 + 2 shouldBe 4
    }
})
