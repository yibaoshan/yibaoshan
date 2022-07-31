//
// Created by Bob on 2022/7/30.
//

#include "merge.h"

SurfaceFlinger {
    frameworks/native/services/surfaceflinger/MessageQueue.cpp
        ->init()
        ->waitMessage()
        ->invalidate()
}